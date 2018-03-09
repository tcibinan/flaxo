package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.core.env.Environment
import com.tcibinan.flaxo.core.language.Language
import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.model.CourseStatus
import com.tcibinan.flaxo.model.DataService
import com.tcibinan.flaxo.model.EntityAlreadyExistsException
import com.tcibinan.flaxo.model.IntegratedService
import com.tcibinan.flaxo.model.data.StudentTask
import com.tcibinan.flaxo.model.data.Task
import com.tcibinan.flaxo.model.data.User
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
            logger.info("User with $nickname nickname is already registered")
            responseService.response(USER_ALREADY_EXISTS, "User $nickname")
        } catch (e: Throwable) {
            logger.info("Unexpected server error while registering user " +
                    "with $nickname nickname. Cause: " + e.message)
            responseService.response(SERVER_ERROR, e.message)
        }
    }

    @PostMapping("/createCourse")
    @PreAuthorize("hasAuthority('USER')")
    fun createCourse(@RequestParam courseName: String,
                     @RequestParam language: String,
                     @RequestParam testLanguage: String,
                     @RequestParam testingFramework: String,
                     @RequestParam numberOfTasks: Int,
                     principal: Principal
    ): Response {
        val user = dataService.getUser(principal.name)
                ?: return responseService.response(USER_NOT_FOUND, principal.name)

        val githubToken = user.credentials.githubToken ?: return responseService.response(NO_GITHUB_KEY)

        val environment = environmentService.produceEnvironment(
                language,
                testLanguage,
                testingFramework
        )

        val git = gitService.with(githubToken)

        git.createRepository(courseName)
                .createBranch("prerequisites")
                .fillWith(environment)
                .createSubBranches(numberOfTasks, tasksPrefix)

        git.addWebHook(courseName)

        val course = dataService.createCourse(
                courseName,
                language,
                testLanguage,
                testingFramework,
                tasksPrefix,
                numberOfTasks,
                user
        )

        return responseService.response(COURSE_CREATED, courseName, payload = course)
    }

    @PostMapping("/deleteCourse")
    @PreAuthorize("hasAuthority('USER')")
    fun deleteCourse(@RequestParam courseName: String,
                     principal: Principal
    ): Response {
        val user = dataService.getUser(principal.name)
                ?: return responseService.response(USER_NOT_FOUND, principal.name)

        val githubToken = user.credentials.githubToken ?: return responseService.response(NO_GITHUB_KEY)

        dataService.deleteCourse(courseName, user)

        gitService.with(githubToken).deleteRepository(courseName)

        return responseService.response(COURSE_DELETED, courseName)
    }

    @PostMapping("/composeCourse")
    @PreAuthorize("hasAuthority('USER')")
    fun composeCourse(@RequestParam courseName: String,
                      principal: Principal
    ): Response {
        val user = dataService.getUser(principal.name)
                ?: return responseService.response(USER_NOT_FOUND, principal.name)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.response(COURSE_NOT_FOUND, principal.name, courseName)

        val githubToken = user.credentials.githubToken
                ?: return responseService.response(NO_GITHUB_KEY)

        val githubUserId = user.githubId
                ?: throw Exception("Github id for ${principal.name} is not set.")

        travisService.travis(retrieveTravisToken(user, githubUserId, githubToken))
                .activate(githubUserId, course.name)
                .getOrElseThrow { errorBody ->
                    Exception("Travis activation of $githubUserId/${course.name} repository went bad due to: ${errorBody.string()}")
                }

        dataService.updateCourse(course.with(status = CourseStatus.RUNNING))

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

    @GetMapping("/{owner}/{course}/statistics")
    @PreAuthorize("hasAuthority('USER')")
    fun getCourseStatistics(@PathVariable("owner") ownerNickname: String,
                            @PathVariable("course") courseName: String
    ): Response {
        val user = dataService.getUser(ownerNickname)
                ?: return responseService.response(USER_NOT_FOUND, ownerNickname)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.response(COURSE_NOT_FOUND, ownerNickname, courseName)

        return responseService.response(
                COURSE_STATISTICS,
                payload = course.students
                        .map { it.nickname to it.studentTasks.reports() }
                        .toMap()
        )
    }

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

    @GetMapping("/supportedLanguages")
    fun supportedLanguages(): Response =
            responseService.response(SUPPORTED_LANGUAGES, payload = supportedLanguages.flatten())

    @PostMapping("/analysePlagiarism")
    @PreAuthorize("hasAuthority('USER')")
    fun analysePlagiarism(@RequestParam courseName: String,
                          principal: Principal
    ): Response {
        val user = dataService.getUser(principal.name)
                ?: throw Exception("User with the required nickname ${principal.name} wasn't found.")

        val course = dataService.getCourse(courseName, user)
                ?: throw Exception("Course $courseName wasn't found for user ${principal.name}.")

        val mossTasks: List<MossTask> = mossService.extractMossTasks(course)

        synchronized(executor) {
            mossTasks.forEach { mossTask ->
                executor.execute {
                    val taskShortName: String = mossTask.taskName.split("/").last()

                    val task: Task =
                            course.tasks
                                    .find { it.name == taskShortName }
                                    ?: throw Exception("Moss task ${mossTask.taskName} aim course task $taskShortName " +
                                            "wasn't found for course ${course.name}")

                    val result: MossResult = mossService.client(course.language)
                            .base(mossTask.base)
                            .solutions(mossTask.solutions)
                            .analyse()

                    dataService.updateTask(task.with(mossUrl = result.url.toString()))
                }
            }
        }

        return responseService.response(
                PLAGIARISM_ANALYSIS_SCHEDULED,
                mossTasks.map { it.taskName }.toString()
        )
    }
}

private fun Collection<StudentTask>.reports(): List<Any> =
        map {
            mapOf(
                    "builded" to it.anyBuilds,
                    "succeed" to it.buildSucceed
            )
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

fun Branch.fillWith(environment: Environment): Branch {
    environment.getFiles().forEach { load(it) }
    return this
}