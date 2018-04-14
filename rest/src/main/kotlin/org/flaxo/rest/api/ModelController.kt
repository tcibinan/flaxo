package org.flaxo.rest.api

import org.flaxo.core.language.Language
import org.flaxo.model.CourseStatus
import org.flaxo.model.DataService
import org.flaxo.model.EntityAlreadyExistsException
import org.flaxo.model.EntityNotFound
import org.flaxo.model.IntegratedService
import org.flaxo.model.data.Task
import org.flaxo.model.data.User
import org.flaxo.model.data.toViews
import org.flaxo.moss.MossException
import org.flaxo.moss.MossResult
import org.flaxo.rest.service.environment.RepositoryEnvironmentService
import org.flaxo.rest.service.git.GitService
import org.flaxo.rest.service.moss.MossService
import org.flaxo.rest.service.moss.MossTask
import org.flaxo.rest.service.response.ResponseService
import org.flaxo.rest.service.travis.TravisService
import org.flaxo.travis.Travis
import org.flaxo.travis.TravisException
import org.flaxo.travis.TravisUser
import io.vavr.control.Either
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@RestController
@RequestMapping("/rest")
class ModelController @Autowired constructor(private val dataService: DataService,
                                             private val responseService: ResponseService,
                                             private val environmentService: RepositoryEnvironmentService,
                                             private val travisService: TravisService,
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
                courseName,
                language,
                testLanguage,
                testingFramework,
                tasksPrefix,
                numberOfTasks,
                user
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

        val githubToken = user.credentials.githubToken
                ?: return responseService.githubTokenNotFound(principal.name)

        logger.info("Deleting course ${principal.name}/$courseName from the database")

        dataService.deleteCourse(courseName, user)

        logger.info("Deleting course ${principal.name}/$courseName git repository")

        gitService.with(githubToken).deleteRepository(courseName)

        logger.info("Course ${principal.name}/$courseName has been successfully deleted")

        return responseService.ok()
    }

    /**
     * Composes course and enable travis builds on the git repository pull requests.
     *
     * It is called after the teacher has finished creating tasks.
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

        logger.info("Initialising travis client for ${user.nickname} user")

        val travis = travisService.travis(retrieveTravisToken(user, githubUserId, githubToken))

        logger.info("Retrieving travis user for ${user.nickname} user")

        val travisUser: TravisUser = travis.getUser()
                .getOrElseExecute { errorBody ->
                    return responseService.serverError(
                            "Travis user retrieving for ${user.nickname} went bad due to: $errorBody"
                    )
                }

        logger.info("Trigger travis user with id ${travisUser.id} sync for ${user.nickname} user")

        travis.sync(travisUser.id)
                ?.also { errorBody ->
                    throw TravisException("Travis user ${travisUser.id} sync hasn't started due to: ${errorBody.string()}")
                }

        logger.info("Trying to ensure that current user's travis synchronisation has finished")

        travis.waitUntilTravisSynchronisationWillBeFinishedFor(travisUser.id)

        logger.info("Activating git repository of the course ${user.nickname}/$courseName for travis CI")

        travis.activate(githubUserId, course.name)
                .getOrElseThrow { errorBody ->
                    TravisException("Travis activation of $githubUserId/${course.name} " +
                            "repository went bad due to: ${errorBody.string()}")
                }

        logger.info("Changing course ${user.nickname}/$courseName status to running")

        dataService.updateCourse(course.copy(status = CourseStatus.RUNNING))

        logger.info("Course ${user.nickname}/$courseName has been successfully composed")

        return responseService.ok()
    }

    private fun retrieveTravisToken(user: User,
                                    githubUserId: String,
                                    githubToken: String
    ): String = retrieveUserWithTravisToken(user, githubUserId, githubToken)
            .credentials
            .travisToken
            ?: throw TravisException("Travis token wasn't found for ${user.nickname}.")

    private fun retrieveUserWithTravisToken(user: User,
                                            githubUserId: String,
                                            githubToken: String
    ): User = user
            .takeUnless { it.credentials.travisToken.isNullOrBlank() }
            ?: dataService.addToken(
                    user.nickname,
                    IntegratedService.TRAVIS,
                    travisService.retrieveTravisToken(githubUserId, githubToken)
            )

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
                ?: throw EntityNotFound("User ${principal.name}")

        val course = dataService.getCourse(courseName, user)
                ?: throw EntityNotFound("Course ${user.nickname}/$courseName")

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
                responseService.ok(dataService.getCourses(nickname).toViews())
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
        logger.info("Trying to aggregate course $ownerNickname/$courseName statistics")

        val user = dataService.getUser(ownerNickname)
                ?: return responseService.userNotFound(ownerNickname)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.courseNotFound(ownerNickname, courseName)

        logger.info("Aggregating course $ownerNickname/$courseName students statistics")

        val studentsStatistics: Map<String, List<Any>> = course.students
                .map { it.nickname to it.solutions.toViews() }
                .toMap()

        logger.info("Aggregating course $ownerNickname/$courseName tasks statistics")

        val tasksStatistics: Map<String, Any> = course.tasks
                .map { task ->
                    task.name to object {
                        val mossResultUrl = task.mossUrl
                        val mossPlagiarismMatches =
                                task.mossUrl
                                        ?.let { mossService.retrieveAnalysisResult(it) }
                                        ?.matches()
                                        .orEmpty()
                    }
                }
                .toMap()

        logger.info("Course $ownerNickname/$courseName statistics has been successfully aggregated")

        return responseService.ok(object {
            val perStudentStats = studentsStatistics
            val perTaskStats = tasksStatistics
        })
    }

    /**
     * Returns all tasks of the course.
     *
     * @param ownerNickname Course owner nickname.
     * @param courseName Name of the course and related git repository.
     */
    @GetMapping("/{owner}/{course}/tasks")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional(readOnly = true)
    fun getCourseTasks(@PathVariable("owner") ownerNickname: String,
                       @PathVariable("course") courseName: String
    ): ResponseEntity<Any> {
        val user = dataService.getUser(ownerNickname)
                ?: return responseService.userNotFound(ownerNickname)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.courseNotFound(ownerNickname, courseName)

        return responseService.ok(course.tasks.map { it.name })
    }

    /**
     * Returns all students of the course.
     *
     * @param ownerNickname Course owner nickname.
     * @param courseName Name of the course and related git repository.
     */
    @GetMapping("/{owner}/{course}/students")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional(readOnly = true)
    fun getCourseStudents(@PathVariable("owner") ownerNickname: String,
                          @PathVariable("course") courseName: String
    ): ResponseEntity<Any> {
        val user = dataService.getUser(ownerNickname)
                ?: return responseService.userNotFound(ownerNickname)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.courseNotFound(ownerNickname, courseName)

        return responseService.ok(course.students.map { it.nickname })
    }

    /**
     * Returns a list of supported languages by flaxo.
     */
    @GetMapping("/supportedLanguages")
    fun supportedLanguages(): ResponseEntity<Any> =
            responseService.ok(supportedLanguages.flatten())

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
                    val taskShortName: String = mossTask.taskName.split("/").last()

                    val task: Task =
                            course.tasks
                                    .find { it.name == taskShortName }
                                    ?: throw MossException("Moss task ${mossTask.taskName} aim course task $taskShortName " +
                                            "wasn't found for course ${course.name}")

                    logger.info("Analysing ${mossTask.taskName} moss task")

                    val result: MossResult = mossService.client(course.language)
                            .base(mossTask.base)
                            .solutions(mossTask.solutions)
                            .analyse()

                    logger.info("Moss task analysis ${mossTask.taskName} has finished successfully")

                    dataService.updateTask(task.copy(mossUrl = result.url.toString()))
                }
            }
        }

        logger.info("Moss plagiarism analysis has been started for ${principal.name}/$courseName")

        val scheduledTasksNames: List<String> =
                mossTasks.map { it.taskName }
                        .map { it.split("/").last() }

        return responseService.ok("Plagiarism analysis scheduled for tasks: $scheduledTasksNames")
    }

    private fun Travis.waitUntilTravisSynchronisationWillBeFinishedFor(travisUserId: String,
                                                                                        attemptsLimit: Int = 20,
                                                                                        retrievingDelay: Long = 3000
    ) {
        val observationDuration: (Int) -> Long = { attempt -> (attempt + 1) * retrievingDelay / 1000 }

        repeat(attemptsLimit) { attempt ->
            Thread.sleep(retrievingDelay)

            val travisUser1 = getUser()
                    .getOrElseThrow { errorBody ->
                        TravisException("Travis user $travisUserId retrieving went bad due to: ${errorBody.string()}")
                    }

            if (travisUser1.isSyncing) logger.info("Travis user $travisUserId synchronisation hasn't finished " +
                    "after ${observationDuration(attempt)} seconds.")
            else return
        }

        throw TravisException("Travis synchronisation hasn't finished " +
                "after ${observationDuration(attemptsLimit)} seconds.")
    }

    private inline fun <L, R> Either<L, R>.getOrElseExecute(block: (L) -> Unit): R =
            apply { if (isLeft) block(left) }.get()

    private fun Map<String, Language>.flatten(): List<Any> =
            map { (name, language) ->
                mapOf(
                        "name" to name,
                        "compatibleTestingLanguages"
                                to language.compatibleTestingLanguages.map { it.name },
                        "compatibleTestingFrameworks"
                                to language.compatibleTestingFrameworks.map { it.name }
                )
            }

}