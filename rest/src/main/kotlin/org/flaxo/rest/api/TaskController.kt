package org.flaxo.rest.api

import org.apache.logging.log4j.LogManager
import org.flaxo.common.FlaxoException
import org.flaxo.common.data.SolutionReview
import org.flaxo.git.AddReviewRequest
import org.flaxo.git.PullRequestReviewStatus
import org.flaxo.model.DataManager
import org.flaxo.model.SolutionView
import org.flaxo.model.TaskView
import org.flaxo.model.data.Course
import org.flaxo.model.data.Solution
import org.flaxo.model.data.Task
import org.flaxo.rest.manager.github.GithubManager
import org.flaxo.rest.manager.notification.NotificationManager
import org.flaxo.rest.manager.notification.SolutionNotification
import org.flaxo.rest.manager.response.Response
import org.flaxo.rest.manager.response.ResponseManager
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.time.LocalDate
import java.time.LocalTime

/**
 * Tasks handling controller.
 */
@RestController
@RequestMapping("/rest/task")
class TaskController(private val dataManager: DataManager,
                     private val githubManager: GithubManager,
                     private val responseManager: ResponseManager,
                     private val notificationManager: NotificationManager
) {

    private val logger = LogManager.getLogger(TaskController::class.java)

    /**
     * Update task rules.
     *
     * @param courseName Name of the course.
     * @param taskBranch Name of the branch related to exact task.
     * @param deadline Updated task deadline.
     */
    @PostMapping("/update/rules")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun updateRules(@RequestParam courseName: String,
                    @RequestParam taskBranch: String,
                    @RequestParam(required = false) deadline: String?,
                    principal: Principal
    ): Response<TaskView> {
        logger.info("Updating rules of ${principal.name}/$courseName/$taskBranch task")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(principal.name, courseName)

        val task = course.tasks
                .find { it.branch == taskBranch }
                ?: return responseManager.taskNotFound(principal.name, courseName, taskBranch)

        val updatedTask =
                if (deadline == null) {
                    dataManager.updateTask(task.copy(deadline = null))
                } else {
                    dataManager.updateTask(task.copy(deadline = LocalDate.parse(deadline).atTime(LocalTime.MAX)))
                }

        return responseManager.ok(updatedTask.view())
    }

    /**
     * Update task solution scores.
     *
     * @param courseName Name of the course.
     * @param taskBranch Name of the task.
     * @param scores Updates students scores.
     */
    @PostMapping("/update/scores")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun updateScores(@RequestParam courseName: String,
                     @RequestParam taskBranch: String,
                     @RequestBody scores: Map<String, Int>,
                     principal: Principal
    ): Response<List<SolutionView>> {
        logger.info("Updating scores for ${principal.name}/$courseName/$taskBranch task: $scores")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(principal.name, courseName)

        val task = course.tasks
                .find { it.branch == taskBranch }
                ?: return responseManager.taskNotFound(principal.name, courseName, taskBranch)

        val updatedSolutions = task.solutions
                .asSequence()
                .map { it to scores[it.student.name] }
                .filter { (_, updatedScore) -> updatedScore != null }
                .filter { (_, updatedScore) -> updatedScore in 0..100 }
                .filter { (solution, updatedScore) -> solution.score != updatedScore }
                .map { (solution, updatedScore) -> solution.copy(score = updatedScore) }
                .map { dataManager.updateSolution(it) }

        if (course.settings.notificationOnScoreChange) {
            notificationManager.notify(course, notifications(updatedSolutions, course))
        }

        return responseManager.ok(mergedSolutions(task.solutions, updatedSolutions))
    }

    private fun notifications(solutions: Sequence<Solution>, course: Course) =
            solutions.mapNotNull { notification(course, it) }.toList()

    private fun notification(course: Course, solution: Solution): SolutionNotification? = solution.score?.let {
        val template = course.settings.scoreChangeNotificationTemplate
                ?: "Solution is accepted. **Your score**: %s / 100."
        SolutionNotification(solution, template.format(solution.score))
    }

    private fun mergedSolutions(originalSolutions: Set<Solution>, updatedSolutions: Sequence<Solution>)
            : List<SolutionView> = originalSolutions.asSequence()
            .map { originalSolution -> updatedSolutions.find { it.id == originalSolution.id } ?: originalSolution }
            .map { it.view() }
            .toList()

    /**
     * Update task solution approvals.
     *
     * @param courseName Name of the course.
     * @param taskBranch Name of the task.
     * @param scores Updates students scores.
     * @return List of *updated* solutions only.
     */
    @PostMapping("/update/approvals")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun updateApprovals(@RequestParam courseName: String,
                        @RequestParam taskBranch: String,
                        @RequestBody approvals: Map<String, SolutionReview>,
                        principal: Principal
    ): Response<List<SolutionView>> {
        logger.info("Updating approvals for ${principal.name}/$courseName/$taskBranch task: $approvals")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val githubToken = user.credentials.githubToken
                ?: return responseManager.githubTokenNotFound(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(principal.name, courseName)

        val task = course.tasks
                .find { it.branch == taskBranch }
                ?: return responseManager.taskNotFound(principal.name, courseName, taskBranch)

        logger.info("Creating new pull request reviews for ${principal.name}/$courseName/$taskBranch task")

        val repository = githubManager.with(githubToken)
                .getRepository(course.name)

        repository.getPullRequests().asSequence()
                .filter { it.targetBranch == task.branch }
                .filter { it.authorLogin in approvals }
                .filter { pullRequest ->
                    pullRequest.number == solutionPullRequestNumber(task, pullRequest.authorLogin)
                }
                .map { pullRequest ->
                    val pullRequestAuthor = pullRequest.authorLogin
                    val solutionReview = approvals[pullRequestAuthor]
                            ?: throw FlaxoException("Approval authored by $pullRequestAuthor wasn't found")
                    AddReviewRequest(
                            pullRequestId = pullRequest.id,
                            body = solutionReview.body,
                            state = solutionReview.approved.toReviewStatus()
                    )
                }
                .forEach { repository.addPullRequestReview(it) }

        logger.info("Updating solution models to the latest approvals")

        val updatedSolutions: List<SolutionView> =
                task.solutions.asSequence()
                        .filter { it.student.name in approvals }
                        .map { it.copy(approved = approvals[it.student.name]?.approved!!) }
                        .map { dataManager.updateSolution(it) }
                        .map { it.view() }
                        .toList()

        return responseManager.ok(updatedSolutions)
    }

    private fun solutionPullRequestNumber(task: Task, student: String): Int? =
            task.solutions.find { it.student.name == student }
                    ?.commits
                    ?.lastOrNull()
                    ?.pullRequestNumber

    private fun Boolean.toReviewStatus(): PullRequestReviewStatus =
            if (this) PullRequestReviewStatus.APPROVED
            else PullRequestReviewStatus.CHANGES_REQUESTED
}
