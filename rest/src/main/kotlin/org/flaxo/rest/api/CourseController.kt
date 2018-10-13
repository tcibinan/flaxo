package org.flaxo.rest.api

import arrow.core.Try
import arrow.core.getOrElse
import org.apache.logging.log4j.LogManager
import org.flaxo.common.CourseLifecycle
import org.flaxo.common.ExternalService
import org.flaxo.core.stringStackTrace
import org.flaxo.model.CourseView
import org.flaxo.model.DataManager
import org.flaxo.model.data.Course
import org.flaxo.model.data.PlagiarismMatch
import org.flaxo.model.data.Task
import org.flaxo.model.data.views
import org.flaxo.moss.MossException
import org.flaxo.rest.manager.ValidationManager
import org.flaxo.rest.manager.codacy.CodacyManager
import org.flaxo.rest.manager.environment.EnvironmentManager
import org.flaxo.rest.manager.github.GithubManager
import org.flaxo.rest.manager.moss.MossManager
import org.flaxo.rest.manager.moss.MossSubmission
import org.flaxo.rest.manager.response.Response
import org.flaxo.rest.manager.response.ResponseManager
import org.flaxo.rest.manager.travis.TravisManager
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Files
import java.security.Principal
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Courses handling controller.
 */
@RestController
@RequestMapping("/rest/course")
class CourseController(private val dataManager: DataManager,
                       private val responseManager: ResponseManager,
                       private val environmentManager: EnvironmentManager,
                       private val travisManager: TravisManager,
                       private val codacyManager: CodacyManager,
                       private val githubManager: GithubManager,
                       private val mossManager: MossManager,
                       private val courseValidations: Map<ExternalService, ValidationManager>
) {

    private val tasksPrefix = "task-"
    private val logger = LogManager.getLogger(CourseController::class.java)
    private val executor: Executor = Executors.newCachedThreadPool()

    /**
     * Imports a course from an existing git repository or the [principal] by its [repositoryName].
     */
    @PostMapping("/import")
    fun import(@RequestParam repositoryName: String,
               @RequestParam(required = false) description: String?,
               @RequestParam language: String,
               @RequestParam testingLanguage: String,
               @RequestParam testingFramework: String,
               principal: Principal
    ): Response<CourseView> {
        logger.info("Trying to import course ${principal.name}/$repositoryName from an existing repository")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val githubId = user.githubId
                ?: return responseManager.githubIdNotFound(user.nickname)

        val githubToken = user.credentials.githubToken
                ?: return responseManager.githubTokenNotFound(user.nickname)

        return githubManager.with(githubToken)
                .getRepository(repositoryName)
                .takeIf { it.exists() }
                ?.let { repository ->
                    logger.info("Scanning for tasks of the importing course ${principal.name}/$repositoryName")
                    repository.branches()
                            .map { it.name }
                            .filter { it.startsWith(tasksPrefix) }
                            .let { tasksNames ->
                                dataManager.createCourse(
                                        repositoryName,
                                        description,
                                        language,
                                        testingLanguage,
                                        testingFramework,
                                        tasksNames,
                                        user
                                )
                            }
                }
                ?.let { responseManager.ok(it.view()) }
                ?: responseManager.bad("Github repository $githubId/$repositoryName doesn't exist")
    }

    /**
     * Creates a course and related git repository.
     *
     * @param courseName Name of the course and related git repository.
     * @param description Optional course description.
     * @param language Language of the course.
     * @param testLanguage Language used for tests.
     * @param testingFramework Testing framework which is used for testing.
     * @param numberOfTasks Number of tasks in the course and number of tasks branches
     * in the git repository.
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun createCourse(@RequestParam courseName: String,
                     @RequestParam(required = false) description: String?,
                     @RequestParam language: String,
                     @RequestParam testingLanguage: String,
                     @RequestParam testingFramework: String,
                     @RequestParam numberOfTasks: Int,
                     principal: Principal
    ): Response<CourseView> {
        logger.info("Trying to create course ${principal.name}/$courseName")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val githubToken = user.credentials.githubToken
                ?: return responseManager.githubTokenNotFound(principal.name)

        logger.info("Producing course ${principal.name}/$courseName " +
                "environment: $language, $testingLanguage, $testingFramework")

        val environment = environmentManager.produceEnvironment(
                language,
                testingLanguage,
                testingFramework
        )

        logger.info("Creating git repository for course ${principal.name}/$courseName")

        githubManager.with(githubToken)
                .createRepository(courseName).apply {
                    createBranch("prerequisites")
                            .also { branch -> environment.files().forEach { branch.commit(it) } }
                            .createSubBranches(numberOfTasks, tasksPrefix)
                    addWebHook()
                }

        logger.info("Creating course ${principal.name}/$courseName in database")

        val course = dataManager.createCourse(
                courseName = courseName,
                description = description,
                language = language,
                testingLanguage = testingLanguage,
                testingFramework = testingFramework,
                tasksPrefix = tasksPrefix,
                numberOfTasks = numberOfTasks,
                owner = user
        )

        logger.info("Course ${principal.name}/${course.name} has been successfully created")

        return responseManager.ok(course.view())
    }

    /**
     * Returns full information about the current user's course with the given [courseName].
     *
     * @param courseName Name of the course and related git repository.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    @Transactional(readOnly = true)
    fun course(@RequestParam courseName: String,
               principal: Principal
    ): Response<CourseView> {
        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(principal.name, courseName)

        return responseManager.ok(course.view())
    }

    /**
     * Returns full information about all courses of the given user.
     *
     * @param nickname User nickname to retrieve courses from.
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional(readOnly = true)
    fun allCourses(@RequestParam nickname: String,
                   principal: Principal
    ): Response<List<CourseView>> =
            if (principal.name == nickname) {
                responseManager.ok(dataManager.getCourses(nickname).views())
            } else {
                responseManager.forbidden()
            }

    /**
     * Deletes a current user course from the flaxo system and delete git repository as well.
     *
     * @param courseName Name of the course and related git repository.
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun deleteCourse(@RequestParam courseName: String,
                     principal: Principal
    ): Response<CourseView> {
        logger.info("Trying to delete course ${principal.name}/$courseName")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(principal.name, courseName)

        val githubToken = user.credentials.githubToken
                ?: return responseManager.githubTokenNotFound(principal.name)

        logger.info("Deactivating validations for ${principal.name}/$courseName course")

        course.state
                .activatedServices
                .mapNotNull { courseValidations[it] }
                .forEach { it.deactivate(course) }

        logger.info("Deleting course ${principal.name}/$courseName from the database")

        dataManager.deleteCourse(courseName, user)

        logger.info("Deleting course ${principal.name}/$courseName git repository")

        githubManager.with(githubToken)
                .getRepository(courseName)
                .delete()

        logger.info("Course ${principal.name}/$courseName has been successfully deleted")

        return responseManager.ok(course.view())
    }

    /**
     * Composes course and enable travis builds and codacy analysis on
     * the git repository pull requests.
     *
     * Travis and codacy analysis is only activated if user has authorized
     * with related services in flaxo.
     *
     * It is supposed to be called after a user has finished filling tasks.
     *
     * @param courseName Name of the course and related git repository.
     */
    @PostMapping("/activate")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun activate(@RequestParam courseName: String,
                 principal: Principal
    ): Response<CourseView> {
        logger.info("Trying to compose course ${principal.name}/$courseName")

        val user = dataManager.getUser(principal.name)
                ?.also {
                    it.credentials.githubToken ?: return responseManager.githubTokenNotFound(it.nickname)
                    it.githubId ?: return responseManager.githubIdNotFound(it.nickname)
                }
                ?: return responseManager.userNotFound(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(principal.name, courseName)

        val activatedServices = courseValidations
                .mapNotNull { (serviceType, service) ->
                    Try { service.activate(course) }
                            .map { serviceType }
                            .getOrElse {
                                logger.info("$serviceType activation went bad for ${user.nickname}/$courseName " +
                                        "course due to: ${it.stringStackTrace()}")
                                null
                            }
                }
                .toSet()

        logger.info("Changing course ${user.nickname}/$courseName status to running " +
                "with activated services: $activatedServices")

        val activatedCourse = dataManager.updateCourse(course.copy(
                state = course.state.copy(
                        lifecycle = CourseLifecycle.RUNNING,
                        activatedServices = activatedServices
                )
        ))

        logger.info("Course ${user.nickname}/$courseName has been successfully composed")

        return responseManager.ok(activatedCourse.view())
    }

    /**
     * Activates codacy validations for specified [courseName].
     *
     * Course should be started.
     *
     * @param courseName Course name to activate codacy for.
     */
    @PostMapping("/activate/codacy")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun activateCodacy(@RequestParam courseName: String,
                       principal: Principal
    ): Response<CourseView> {
        logger.info("Trying to activate codacy for course ${principal.name}/$courseName")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(principal.name, courseName)

        if (course.state.lifecycle != CourseLifecycle.RUNNING)
            return responseManager.bad("Course ${principal.name}/$courseName is not started yet")

        course.state
                .activatedServices
                .takeUnless { it.contains(ExternalService.CODACY) }
                ?.let {
                    try {
                        codacyManager.activate(course)

                        return dataManager
                                .updateCourse(course.copy(
                                        state =
                                        course.state.copy(
                                                activatedServices =
                                                course.state.activatedServices + ExternalService.CODACY
                                        )
                                ))
                                .let { responseManager.ok(it.view()) }
                    } catch (e: Exception) {
                        logger.info("Codacy activation failed due to: $e")
                        return responseManager.bad(e.toString())
                    }
                }
                ?: return responseManager.bad("Codacy is already integrated with " +
                        "${principal.name}/$courseName course")
    }

    /**
     * Activates travis validations for specified [courseName].
     *
     * Course should be started.
     *
     * @param courseName Course name to activate travis for.
     */
    @PostMapping("/activate/travis")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun activateTravis(@RequestParam courseName: String,
                       principal: Principal
    ): Response<CourseView> {
        logger.info("Trying to activate travis for course ${principal.name}/$courseName")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(principal.name, courseName)

        if (course.state.lifecycle != CourseLifecycle.RUNNING) {
            return responseManager.bad("Course ${principal.name}/$courseName is not started yet")
        }

        course.state
                .activatedServices
                .takeUnless { it.contains(ExternalService.TRAVIS) }
                ?.let {
                    try {
                        travisManager.activate(course)

                        return dataManager
                                .updateCourse(course.copy(
                                        state =
                                        course.state.copy(
                                                activatedServices =
                                                course.state.activatedServices + ExternalService.TRAVIS
                                        )
                                ))
                                .let { responseManager.ok(it.view()) }
                    } catch (e: Exception) {
                        logger.info("Travis activation failed due to: $e")
                        return responseManager.bad(e.toString())
                    }
                }
                ?: return responseManager.bad("Travis is already integrated with " +
                        "${principal.name}/$courseName course")
    }

    /**
     * Starts current user's [courseName] plagiarism analysis.
     *
     * @param courseName Name of the course and related git repository.
     */
    @PostMapping("/analyse/plagiarism")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun analysePlagiarism(@RequestParam courseName: String,
                          principal: Principal
    ): Response<Unit> {
        logger.info("Trying to start plagiarism analysis for ${principal.name}/$courseName")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(principal.name, courseName)

        logger.info("Extracting moss submissions for ${user.nickname}/$courseName")

        val mossSubmissions: List<MossSubmission> = mossManager.extractSubmissions(course)

        logger.info("${mossSubmissions.size} moss tasks were extracted for ${user.nickname}/$courseName")

        logger.info("Scheduling moss submissions for ${user.nickname}/$courseName")

        val submittedTasks = submitMossTasksExecution(mossSubmissions, course)

        logger.info("Moss plagiarism analysis has been started for ${principal.name}/$courseName")

        Try {
            CompletableFuture.allOf(*submittedTasks).get()
        }.getOrElse { e ->
            logger.error("Moss plagiarism analysis went bad for some of the submissions: ${e.stringStackTrace()}")
        }

        return responseManager.ok()
    }

    private fun submitMossTasksExecution(mossSubmissions: List<MossSubmission>,
                                         course: Course
    ): Array<CompletableFuture<Void>> = synchronized(executor) {
        mossSubmissions.map { mossTask ->
            CompletableFuture.runAsync(Runnable { analyseMossTask(mossTask, course, course.tasks) }, executor)
        }
    }.toTypedArray()

    private fun analyseMossTask(submission: MossSubmission, course: Course, courseTasks: Set<Task>) {
        val branch = submission.branch

        val task = courseTasks.find { it.branch == branch }
                ?: throw MossException("Moss submission ${submission.id} " +
                        "target task ${course.name}/$branch was not found ")

        logger.info("Starting moss submission ${submission.id} for " +
                "${submission.base.size} bases files " +
                "and ${submission.solutions.size} solutions files")

        val mossResult =
                mossManager.client(course.language)
                        .base(submission.base)
                        .solutions(submission.solutions)
                        .analyse()

        logger.info("Moss submission ${submission.id} has finished successfully and is available by ${mossResult.url}")

        val plagiarismReport = dataManager.addPlagiarismReport(
                task = task,
                url = mossResult.url.toString(),
                matches = mossResult.matches().map {
                    PlagiarismMatch(
                            student1 = it.students.first,
                            student2 = it.students.second,
                            lines = it.lines,
                            url = it.link,
                            percentage = it.percentage
                    )
                }
        )

        dataManager.updateTask(task.copy(
                plagiarismReports = task.plagiarismReports.plus(plagiarismReport)
        ))

        logger.info("Deleting moss submission ${submission.id} generated files.")

        (submission.base + submission.solutions)
                .forEach { Files.delete(it.localPath) }
    }

    /**
     * Synchronize course solutions and validations.
     */
    @PostMapping("/sync")
    fun synchronize(@RequestParam courseName: String,
                    principal: Principal
    ): Response<CourseView> {
        logger.info("Syncing ${principal.name}/$courseName course")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val githubToken = user.credentials.githubToken
                ?: return responseManager.githubTokenNotFound(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(principal.name, courseName)

        logger.info("Upserting missing solutions and commits of ${principal.name}/${course.name} course")

        githubManager.with(githubToken)
                .getRepository(course.name)
                .getPullRequests()
                .forEach { githubManager.upsertPullRequest(it) }

        logger.info("Syncing ${principal.name}/$courseName course validation results")

        val courseWithNewSolutions = dataManager.getCourse(course.name, user)
                ?: return responseManager.courseNotFound(principal.name, course.name)

        courseWithNewSolutions.takeIf { it.state.lifecycle == CourseLifecycle.RUNNING }
                ?.state
                ?.activatedServices
                ?.mapNotNull { courseValidations[it] }
                ?.forEach { it.refresh(courseWithNewSolutions) }
                ?: return responseManager.bad("Course ${user.nickname}/${courseWithNewSolutions.name} " +
                        "is not running to be synchronized")

        logger.info("Course validations were synchronized for ${principal.name}/${courseWithNewSolutions.name}")

        val courseWithRefreshedValidations = dataManager.getCourse(courseWithNewSolutions.name, user)
                ?: return responseManager.courseNotFound(principal.name, courseWithNewSolutions.name)

        return responseManager.ok(courseWithRefreshedValidations.view())
    }
}
