package org.flaxo.rest.api

import org.flaxo.core.language.Language
import org.flaxo.model.CourseLifecycle
import org.flaxo.model.DataService
import org.flaxo.model.EntityAlreadyExistsException
import org.flaxo.model.IntegratedService
import org.flaxo.model.data.views
import org.flaxo.moss.MossException
import org.flaxo.rest.service.environment.RepositoryEnvironmentService
import org.flaxo.rest.service.git.GitService
import org.flaxo.rest.service.moss.MossService
import org.flaxo.rest.service.moss.MossTask
import org.flaxo.rest.service.response.ResponseService
import org.flaxo.rest.service.travis.TravisService
import org.flaxo.travis.TravisException
import io.vavr.control.Either
import org.apache.logging.log4j.LogManager
import org.flaxo.codacy.CodacyException
import org.flaxo.model.data.PlagiarismMatch
import org.flaxo.rest.service.codacy.CodacyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Common methods controller.
 */
@RestController
@RequestMapping("/rest")
class ModelController @Autowired constructor(private val dataService: DataService,
                                             private val responseService: ResponseService,
                                             private val environmentService: RepositoryEnvironmentService,
                                             private val travisService: TravisService,
                                             private val codacyService: CodacyService,
                                             private val gitService: GitService,
                                             private val mossService: MossService,
                                             private val supportedLanguages: Map<String, Language>
) {

    private val executor: Executor = Executors.newCachedThreadPool()
    private val logger = LogManager.getLogger(ModelController::class.java)
    private val tasksPrefix = "task-"

    /**
     * Register user in the flaxo system.
     *
     * @param nickname Of the creating user.
     * @param password Of the creating user.
     */
    @PostMapping("/register")
    @Transactional
    fun register(@RequestParam nickname: String,
                 @RequestParam password: String
    ): ResponseEntity<Any> {
        logger.info("Trying to register user $nickname")

        return try {
            dataService.addUser(nickname, password)

            logger.info("User $nickname was registered successfully")

            responseService.ok()
        } catch (e: EntityAlreadyExistsException) {
            logger.info("Trying to create user with $nickname nickname that is already registered")

            responseService.bad("User $nickname already exists")
        }
    }

    /**
     * Returns user account information.
     */
    @GetMapping("/account")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional(readOnly = true)
    fun user(principal: Principal): Any {
        logger.info("Trying to retrieve user ${principal.name}")

        val user = dataService.getUser(principal.name)
                ?: return responseService.userNotFound(principal.name)

        return responseService.ok(user.view())
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
    @PostMapping("/createCourse")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun createCourse(@RequestParam courseName: String,
                     @RequestParam(required = false) description: String?,
                     @RequestParam language: String,
                     @RequestParam testLanguage: String,
                     @RequestParam testingFramework: String,
                     @RequestParam numberOfTasks: Int,
                     principal: Principal
    ): ResponseEntity<Any> {
        logger.info("Trying to create course ${principal.name}/$courseName")

        val user = dataService.getUser(principal.name)
                ?: return responseService.userNotFound(principal.name)

        val githubToken = user.credentials.githubToken
                ?: return responseService.githubTokenNotFound(principal.name)

        logger.info("Producing course ${principal.name}/$courseName " +
                "environment: $language, $testLanguage, $testingFramework")

        val environment = environmentService.produceEnvironment(
                language,
                testLanguage,
                testingFramework
        )

        logger.info("Creating git repository for course ${principal.name}/$courseName")

        gitService.with(githubToken)
                .apply {
                    createRepository(courseName)
                            .createBranch("prerequisites")
                            .also { branch -> environment.getFiles().forEach { branch.load(it) } }
                            .createSubBranches(numberOfTasks, tasksPrefix)

                    addWebHook(courseName)
                }

        logger.info("Creating course ${principal.name}/$courseName in database")

        val course = dataService.createCourse(
                courseName = courseName,
                description = description,
                language = language,
                testingLanguage = testLanguage,
                testingFramework = testingFramework,
                tasksPrefix = tasksPrefix,
                numberOfTasks = numberOfTasks,
                owner = user
        )

        logger.info("Course ${principal.name}/${course.name} has been successfully created")

        return responseService.ok(course.view())
    }

    /**
     * Deletes a current user course from the flaxo system and delete git repository as well.
     *
     * @param courseName Name of the course and related git repository.
     */
    @PostMapping("/deleteCourse")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun deleteCourse(@RequestParam courseName: String,
                     principal: Principal
    ): ResponseEntity<Any> {
        logger.info("Trying to delete course ${principal.name}/$courseName")

        val user = dataService.getUser(principal.name)
                ?: return responseService.userNotFound(principal.name)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.courseNotFound(principal.name, courseName)

        val githubUserId = user.githubId
                ?: return responseService.githubIdNotFound(principal.name)

        val githubToken = user.credentials.githubToken
                ?: return responseService.githubTokenNotFound(principal.name)

        course.state
                .activatedServices
                .takeIf { it.contains(IntegratedService.TRAVIS) }
                ?.let { user.credentials.travisToken }
                ?.also {
                    logger.info("Deactivating travis for ${principal.name}/$courseName course")

                    travisService.travis(it)
                            .deactivate(githubUserId, courseName)
                            .getOrElseThrow { errorBody ->
                                TravisException("Travis deactivation of $githubUserId/$courseName " +
                                        "repository went bad due to: ${errorBody.string()}")
                            }
                }
                ?: logger.info("Travis token wasn't found for ${user.nickname} or travis is not activated " +
                        "with $courseName course so no travis repository is deactivated")

        course.state
                .activatedServices
                .takeIf { it.contains(IntegratedService.CODACY) }
                .let { user.credentials.codacyToken }
                ?.also {
                    logger.info("Deactivating codacy for ${principal.name}/$courseName course")

                    codacyService.codacy(githubUserId, it)
                            .deleteProject(courseName)
                            .also { responseBody ->
                                throw CodacyException("Codacy project $githubUserId/$courseName " +
                                        "deletion went bad due to: $responseBody")
                            }
                }
                ?: logger.info("Codacy token wasn't found for ${user.nickname} or codacy is not activated " +
                        "with $courseName course so no codacy project is deleted")

        logger.info("Deleting course ${principal.name}/$courseName from the database")

        dataService.deleteCourse(courseName, user)

        logger.info("Deleting course ${principal.name}/$courseName git repository")

        gitService.with(githubToken).deleteRepository(courseName)

        logger.info("Course ${principal.name}/$courseName has been successfully deleted")

        return responseService.ok()
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
    @PostMapping("/composeCourse")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun composeCourse(@RequestParam courseName: String,
                      principal: Principal
    ): ResponseEntity<Any> {
        logger.info("Trying to compose course ${principal.name}/$courseName")

        val user = dataService.getUser(principal.name)
                ?: return responseService.userNotFound(principal.name)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.courseNotFound(principal.name, courseName)

        val githubToken = user.credentials.githubToken
                ?: return responseService.githubTokenNotFound(principal.name)

        val githubUserId = user.githubId
                ?: return responseService.githubIdNotFound(user.nickname)

        val activatedServices = mutableSetOf<IntegratedService>()

        try {
            travisService.activateTravis(user, course, githubToken, githubUserId)
            activatedServices.add(IntegratedService.TRAVIS)
        } catch (e: Exception) {
            logger.info("Travis activation went bad for ${user.githubId}/$courseName course" +
                    "due to: ${e.message}")
        }

        try {
            codacyService.activateCodacy(user, course, githubUserId)
            activatedServices.add(IntegratedService.CODACY)
        } catch (e: Exception) {
            logger.info("Codacy activation went bad for $githubUserId/$courseName course" +
                    "due to: ${e.message}")
        }

        logger.info("Changing course ${user.nickname}/$courseName status to running")

        dataService.updateCourse(course.copy(
                state = course.state.copy(
                        lifecycle = CourseLifecycle.RUNNING,
                        activatedServices = activatedServices
                )
        ))

        logger.info("Course ${user.nickname}/$courseName has been successfully composed")

        return responseService.ok()
    }

    @PostMapping("activateCodacy")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun activateCodacy(@RequestParam courseName: String,
                       principal: Principal
    ): ResponseEntity<Any> {
        logger.info("Trying to activate codacy for course ${principal.name}/$courseName")

        val user = dataService.getUser(principal.name)
                ?: return responseService.userNotFound(principal.name)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.courseNotFound(principal.name, courseName)

        val userGithubId = user.githubId
                ?: return responseService.githubIdNotFound(user.nickname)

        course.state
                .activatedServices
                .takeUnless { it.contains(IntegratedService.CODACY) }
                ?.let {
                    try {
                        codacyService
                                .activateCodacy(user, course, userGithubId)

                        return dataService
                                .updateCourse(course.copy(
                                        state =
                                        course.state.copy(
                                                activatedServices =
                                                course.state.activatedServices + IntegratedService.CODACY
                                        )
                                ))
                                .let { responseService.ok(it) }
                    } catch (e: Exception) {
                        logger.info("Codacy activation failed ${e.message}")
                        return responseService.bad(e.message)
                    }
                }
                ?: return responseService.bad("Codacy is already integrated with " +
                        "${principal.name}/$courseName course")
    }

    @PostMapping("activateTravis")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun activateTravis(@RequestParam courseName: String,
                       principal: Principal
    ): ResponseEntity<Any> {
        logger.info("Trying to activate travis for course ${principal.name}/$courseName")

        val user = dataService.getUser(principal.name)
                ?: return responseService.userNotFound(principal.name)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.courseNotFound(principal.name, courseName)

        val userGithubId = user.githubId
                ?: return responseService.githubIdNotFound(user.nickname)

        val githubToken = user.credentials.githubToken
                ?: return responseService.githubTokenNotFound(user.nickname)

        course.state
                .activatedServices
                .takeUnless { it.contains(IntegratedService.TRAVIS) }
                ?.let {
                    try {
                        travisService
                                .activateTravis(user, course, githubToken, userGithubId)

                        return dataService
                                .updateCourse(course.copy(
                                        state =
                                        course.state.copy(
                                                activatedServices =
                                                course.state.activatedServices + IntegratedService.TRAVIS
                                        )
                                ))
                                .let { responseService.ok(it) }
                    } catch (e: Exception) {
                        logger.info("Travis activation failed ${e.message}")
                        return responseService.bad(e.message)
                    }
                }
                ?: return responseService.bad("Travis is already integrated with " +
                        "${principal.name}/$courseName course")
    }

    /**
     * Returns full information about the current user's course with the given [courseName].
     *
     * @param courseName Name of the course and related git repository.
     */
    @GetMapping("course")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional(readOnly = true)
    fun course(@RequestParam courseName: String,
               principal: Principal
    ): ResponseEntity<Any> {
        val user = dataService.getUser(principal.name)
                ?: return responseService.userNotFound(principal.name)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.courseNotFound(principal.name, courseName)

        return responseService.ok(course.view())
    }

    /**
     * Returns full information about all courses of the given user.
     *
     * @param nickname User nickname to retrieve courses from.
     */
    @GetMapping("allCourses")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional(readOnly = true)
    fun allCourses(@RequestParam nickname: String,
                   principal: Principal
    ): ResponseEntity<Any> =
            if (principal.name == nickname) {
                responseService.ok(dataService.getCourses(nickname).views())
            } else {
                responseService.forbidden()
            }

    /**
     * Returns all statistics of the course.
     *
     * @param ownerNickname Course owner nickname.
     * @param courseName Name of the course and related git repository.
     */
    @GetMapping("/{owner}/{course}/statistics")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional(readOnly = true)
    fun getCourseStatistics(@PathVariable("owner") ownerNickname: String,
                            @PathVariable("course") courseName: String
    ): ResponseEntity<Any> {
        logger.info("Aggregating course $ownerNickname/$courseName statistics")

        val user = dataService.getUser(ownerNickname)
                ?: return responseService.userNotFound(ownerNickname)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.courseNotFound(ownerNickname, courseName)

        return responseService.ok(course.tasks.views())
    }

    /**
     * Update task rules.
     *
     * @param courseName Name of the course.
     * @param taskBranch Name of the branch related to exact task.
     * @param deadline Updated task deadline.
     */
    @PutMapping("/updateRules")
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
     * Returns a list of supported languages by flaxo.
     */
    @GetMapping("/supportedLanguages")
    fun supportedLanguages(): ResponseEntity<Any> =
            supportedLanguages
                    .map { (name, language) ->
                        object {
                            val name = name
                            val compatibleTestingLanguages =
                                    language.compatibleTestingLanguages.map { it.name }
                            val compatibleTestingFrameworks =
                                    language.compatibleTestingFrameworks.map { it.name }
                        }
                    }
                    .let { responseService.ok(it) }

    /**
     * Starts current user's course plagiarism analysis.
     *
     * @param courseName Name of the course and related git repository.
     */
    @PostMapping("/analysePlagiarism")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun analysePlagiarism(@RequestParam courseName: String,
                          principal: Principal
    ): ResponseEntity<Any> {
        logger.info("Trying to start plagiarism analysis for ${principal.name}/$courseName")

        val user = dataService.getUser(principal.name)
                ?: return responseService.userNotFound(principal.name)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.courseNotFound(principal.name, courseName)

        logger.info("Extracting moss tasks for ${user.nickname}/$courseName")

        val mossTasks: List<MossTask> = mossService.extractMossTasks(course)

        logger.info("Scheduling moss tasks to execute for ${user.nickname}/$courseName")

        synchronized(executor) {
            mossTasks.forEach { mossTask ->
                executor.execute {
                    val branch = mossTask.taskName.split("/").last()

                    val task =
                            course.tasks
                                    .find { it.branch == branch }
                                    ?: throw MossException("Moss task ${mossTask.taskName} aim course task $branch " +
                                            "wasn't found for course ${course.name}")

                    logger.info("Analysing ${mossTask.taskName} moss task")

                    val mossResult =
                            mossService.client(course.language)
                                    .base(mossTask.base)
                                    .solutions(mossTask.solutions)
                                    .analyse()

                    logger.info("Moss task analysis ${mossTask.taskName} has finished successfully")

                    val plagiarismReport = dataService.addPlagiarismReport(
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

                    dataService.updateTask(task.copy(
                            plagiarismReports = task.plagiarismReports.plus(plagiarismReport)
                    ))
                }
            }
        }

        logger.info("Moss plagiarism analysis has been started for ${principal.name}/$courseName")

        val scheduledTasksNames: List<String> =
                mossTasks.map { it.taskName }
                        .map { it.split("/").last() }

        return responseService.ok("Plagiarism analysis scheduled for tasks: $scheduledTasksNames")
    }

}