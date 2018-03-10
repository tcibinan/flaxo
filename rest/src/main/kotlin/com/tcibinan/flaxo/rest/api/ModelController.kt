package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.core.language.Language
import com.tcibinan.flaxo.model.CourseStatus
import com.tcibinan.flaxo.model.DataService
import com.tcibinan.flaxo.model.EntityAlreadyExistsException
import com.tcibinan.flaxo.model.EntityNotFound
import com.tcibinan.flaxo.model.IntegratedService
import com.tcibinan.flaxo.model.data.Task
import com.tcibinan.flaxo.model.data.User
import com.tcibinan.flaxo.model.data.toViews
import com.tcibinan.flaxo.moss.MossResult
import com.tcibinan.flaxo.rest.api.ServerAnswer.*
import com.tcibinan.flaxo.rest.service.environment.RepositoryEnvironmentService
import com.tcibinan.flaxo.rest.service.git.GitService
import com.tcibinan.flaxo.rest.service.moss.MossService
import com.tcibinan.flaxo.rest.service.moss.MossTask
import com.tcibinan.flaxo.rest.service.response.Response
import com.tcibinan.flaxo.rest.service.response.ResponseService
import com.tcibinan.flaxo.rest.service.travis.TravisService
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
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
    fun register(@RequestParam("nickname") nickname: String,
                 @RequestParam("password") password: String
    ): Response {
        logger.info("Trying to register user $nickname")
        return try {
            dataService.addUser(nickname, password)
            logger.info("User $nickname was registered successfully")
            responseService.response(USER_CREATED, nickname)
        } catch (e: EntityAlreadyExistsException) {
            logger.info("Trying to create user with $nickname nickname that is already registered")
            responseService.response(USER_ALREADY_EXISTS, "User $nickname")
        } catch (e: Throwable) {
            logger.error("Unexpected server error while registering user " +
                    "with $nickname nickname. Cause: " + e.message)
            responseService.response(SERVER_ERROR, e.message)
        }
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
    fun createCourse(@RequestParam courseName: String,
                     @RequestParam language: String,
                     @RequestParam testLanguage: String,
                     @RequestParam testingFramework: String,
                     @RequestParam numberOfTasks: Int,
                     principal: Principal
    ): Response {
        logger.info("Trying to create course ${principal.name}/$courseName")

        val user = dataService.getUser(principal.name)
                ?: return responseService.response(USER_NOT_FOUND, principal.name)

        val githubToken = user.credentials.githubToken
                ?: return responseService.response(NO_GITHUB_KEY)

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

        logger.info("Course ${principal.name}/$courseName has been successfully created")

        return responseService.response(COURSE_CREATED, courseName, payload = course)
    }

    /**
     * Deletes a current user course from the flaxo system and delete git repository as well.
     *
     * @param courseName Name of the course and related git repository.
     */
    @PostMapping("/deleteCourse")
    @PreAuthorize("hasAuthority('USER')")
    fun deleteCourse(@RequestParam courseName: String,
                     principal: Principal
    ): Response {
        logger.info("Trying to delete course ${principal.name}/$courseName")

        val user = dataService.getUser(principal.name)
                ?: return responseService.response(USER_NOT_FOUND, principal.name)

        val githubToken = user.credentials.githubToken
                ?: return responseService.response(NO_GITHUB_KEY)

        logger.info("Deleting course ${principal.name}/$courseName from the database")

        dataService.deleteCourse(courseName, user)

        logger.info("Deleting course ${principal.name}/$courseName git repository")

        gitService.with(githubToken).deleteRepository(courseName)

        logger.info("Course ${principal.name}/$courseName has been successfully deleted")

        return responseService.response(COURSE_DELETED, courseName)
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
    fun composeCourse(@RequestParam courseName: String,
                      principal: Principal
    ): Response {
        logger.info("Trying to compose course ${principal.name}/$courseName")

        val user = dataService.getUser(principal.name)
                ?: return responseService.response(USER_NOT_FOUND, principal.name)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.response(COURSE_NOT_FOUND, principal.name, courseName)

        val githubToken = user.credentials.githubToken
                ?: return responseService.response(NO_GITHUB_KEY)

        val githubUserId = user.githubId
                ?: throw Exception("Github id for ${principal.name} is not set.")

        logger.info("Activating git repository of the course ${principal.name}/$courseName for travis CI")

        travisService.travis(retrieveTravisToken(user, githubUserId, githubToken))
                .activate(githubUserId, course.name)
                .getOrElseThrow { errorBody ->
                    Exception("Travis activation of $githubUserId/${course.name} " +
                            "repository went bad due to: ${errorBody.string()}")
                }

        logger.info("Changing course ${principal.name}/$courseName status to running")

        dataService.updateCourse(course.with(status = CourseStatus.RUNNING))

        logger.info("Course ${principal.name}/$courseName has been successfully composed")

        return responseService.response(COURSE_COMPOSED, courseName)
    }

    private fun retrieveTravisToken(user: User,
                                    githubUserId: String,
                                    githubToken: String
    ): String = retrieveUserWithTravisToken(user, githubUserId, githubToken)
            .credentials
            .travisToken
            ?: throw Exception("Travis token wasn't found for ${user.nickname}.")

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
    fun course(@RequestParam("courseName") courseName: String,
               principal: Principal
    ): Response {
        val user = dataService.getUser(principal.name)
                ?: throw EntityNotFound("User ${principal.name}")

        val course = dataService.getCourse(courseName, user)
                ?: throw EntityNotFound("Course ${user.nickname}/$courseName")

        return responseService.response(COURSES_LIST, payload = course.view())
    }

    /**
     * Returns full information about all courses of the given user.
     *
     * @param nickname User nickname to retrieve courses from.
     */
    @GetMapping("allCourses")
    @PreAuthorize("hasAuthority('USER')")
    fun allCourses(@RequestParam("nickname") nickname: String,
                   principal: Principal
    ): Response =
            if (principal.name == nickname) {
                responseService.response(COURSES_LIST,
                        payload = dataService.getCourses(nickname).toViews()
                )
            } else {
                responseService.response(ANOTHER_USER_DATA, principal.name, nickname)
            }

    /**
     * Returns all statistics of the course.
     *
     * @param ownerNickname Course owner nickname.
     * @param courseName Name of the course and related git repository.
     */
    @GetMapping("/{owner}/{course}/statistics")
    @PreAuthorize("hasAuthority('USER')")
    fun getCourseStatistics(@PathVariable("owner") ownerNickname: String,
                            @PathVariable("course") courseName: String
    ): Response {
        logger.info("Trying to aggregate course $ownerNickname/$courseName statistics")

        val user = dataService.getUser(ownerNickname)
                ?: return responseService.response(USER_NOT_FOUND, ownerNickname)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.response(COURSE_NOT_FOUND, ownerNickname, courseName)

        logger.info("Aggregating course $ownerNickname/$courseName students statistics")

        val studentsStatistics: Map<String, List<Any>> = course.students
                .map { it.nickname to it.studentTasks.toViews() }
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

        return responseService.response(
                COURSE_STATISTICS,
                payload = object {
                    val perStudentStats = studentsStatistics
                    val perTaskStats = tasksStatistics
                }
        )
    }

    /**
     * Returns all tasks of the course.
     *
     * @param ownerNickname Course owner nickname.
     * @param courseName Name of the course and related git repository.
     */
    @GetMapping("/{owner}/{course}/tasks")
    @PreAuthorize("hasAuthority('USER')")
    fun getCourseTasks(@PathVariable("owner") ownerNickname: String,
                       @PathVariable("course") courseName: String
    ): Response {
        val user = dataService.getUser(ownerNickname)
                ?: return responseService.response(USER_NOT_FOUND, ownerNickname)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.response(COURSE_NOT_FOUND, ownerNickname, courseName)

        return responseService.response(
                COURSE_TASKS,
                payload = course.tasks.map { it.name }
        )
    }

    /**
     * Returns all students of the course.
     *
     * @param ownerNickname Course owner nickname.
     * @param courseName Name of the course and related git repository.
     */
    @GetMapping("/{owner}/{course}/students")
    @PreAuthorize("hasAuthority('USER')")
    fun getCourseStudents(@PathVariable("owner") ownerNickname: String,
                          @PathVariable("course") courseName: String
    ): Response {
        val user = dataService.getUser(ownerNickname)
                ?: return responseService.response(USER_NOT_FOUND, ownerNickname)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.response(COURSE_NOT_FOUND, ownerNickname, courseName)

        return responseService.response(
                COURSE_STUDENTS,
                payload = course.students.map { it.nickname }
        )
    }

    /**
     * Returns a list of supported languages by flaxo.
     */
    @GetMapping("/supportedLanguages")
    fun supportedLanguages(): Response =
            responseService.response(SUPPORTED_LANGUAGES, payload = supportedLanguages.flatten())

    /**
     * Starts current user's course plagiarism analysis.
     *
     * @param courseName Name of the course and related git repository.
     */
    @PostMapping("/analysePlagiarism")
    @PreAuthorize("hasAuthority('USER')")
    fun analysePlagiarism(@RequestParam courseName: String,
                          principal: Principal
    ): Response {
        logger.info("Trying to start plagiarism analysis for ${principal.name}/$courseName")

        val user = dataService.getUser(principal.name)
                ?: throw Exception("User with the required nickname ${principal.name} wasn't found.")

        val course = dataService.getCourse(courseName, user)
                ?: throw Exception("Course $courseName wasn't found for user ${principal.name}.")

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
                                    ?: throw Exception("Moss task ${mossTask.taskName} aim course task $taskShortName " +
                                            "wasn't found for course ${course.name}")

                    logger.info("Analysing ${mossTask.taskName} moss task")

                    val result: MossResult = mossService.client(course.language)
                            .base(mossTask.base)
                            .solutions(mossTask.solutions)
                            .analyse()

                    logger.info("Moss task analysis ${mossTask.taskName} has finished successfully")

                    dataService.updateTask(task.with(mossUrl = result.url.toString()))
                }
            }
        }

        logger.info("Moss plagiarism analysis has been started for ${principal.name}/$courseName")

        return responseService.response(
                PLAGIARISM_ANALYSIS_SCHEDULED,
                mossTasks.map { it.taskName }.toString()
        )
    }
}

private fun Map<String, Language>.flatten(): List<Any> =
        map { (name, language) ->
            mapOf(
                    "name" to name,
                    "compatibleTestingLanguages"
                            to language.compatibleTestingLanguages().map { it.name() },
                    "compatibleTestingFrameworks"
                            to language.compatibleTestingFrameworks().map { it.name() }
            )
        }
