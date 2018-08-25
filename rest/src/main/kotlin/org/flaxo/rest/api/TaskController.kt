package org.flaxo.rest.api

import org.apache.logging.log4j.LogManager
import org.flaxo.model.DataService
import org.flaxo.rest.service.response.ResponseService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Tasks handling controller.
 */
@RestController
@RequestMapping("/rest/task")
class TaskController(private val dataService: DataService,
                     private val responseService: ResponseService
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
    ): ResponseEntity<Any> {
        logger.info("Updating rules of ${principal.name}/$courseName/$taskBranch task")

        val user = dataService.getUser(principal.name)
                ?: return responseService.userNotFound(principal.name)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.courseNotFound(principal.name, courseName)

        val task = course.tasks
                .find { it.branch == taskBranch }
                ?: return responseService.taskNotFound(principal.name, courseName, taskBranch)

        return deadline
                ?.let { LocalDate.parse(it) }
                ?.let { LocalDateTime.of(it, LocalTime.MAX) }
                ?.takeIf { it != task.deadline }
                ?.let { dataService.updateTask(task.copy(deadline = it)) }
                ?.let { responseService.ok(it.view()) }
                ?: responseService.ok(task.view())
    }

    /**
     * Update task solutions scores.
     *
     * @param courseName Name of the course.
     * @param taskBranch Name of the branch related to exact task.
     * @param scores Updates students scores.
     */
    @PostMapping("/update/scores")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun updateScores(@RequestParam courseName: String,
                     @RequestParam taskBranch: String,
                     @RequestBody scores: Map<String, Int>,
                     principal: Principal
    ): ResponseEntity<Any> {
        logger.info("Updating scores for ${principal.name}/$courseName/$taskBranch task: $scores")

        val user = dataService.getUser(principal.name)
                ?: return responseService.userNotFound(principal.name)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.courseNotFound(principal.name, courseName)

        val task = course.tasks
                .find { it.branch == taskBranch }
                ?: return responseService.taskNotFound(principal.name, courseName, taskBranch)

        task.solutions
                .map { it to scores[it.student.nickname] }
                .filter { (_, updatedScore) -> updatedScore != null }
                .filter { (_, updatedScore) -> updatedScore in 0..100 }
                .filter { (solution, updatedScore) -> solution.score != updatedScore }
                .map { (solution, updatedScore) -> solution.copy(score = updatedScore) }
                .forEach { dataService.updateSolution(it) }

        return responseService.ok()
    }
}