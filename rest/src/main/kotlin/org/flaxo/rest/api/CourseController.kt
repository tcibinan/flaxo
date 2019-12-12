package org.flaxo.rest.api

import arrow.core.Try
import arrow.core.getOrElse
import org.apache.logging.log4j.LogManager
import org.flaxo.common.data.CourseLifecycle
import org.flaxo.common.data.CourseSettings
import org.flaxo.common.data.ExternalService
import org.flaxo.common.stringStackTrace
import org.flaxo.model.CourseStatisticsView
import org.flaxo.model.CourseView
import org.flaxo.model.DataManager
import org.flaxo.model.data.views
import org.flaxo.rest.friendlyId
import org.flaxo.rest.manager.ValidationManager
import org.flaxo.rest.manager.codacy.CodacyManager
import org.flaxo.rest.manager.course.CourseManager
import org.flaxo.rest.manager.environment.EnvironmentManager
import org.flaxo.rest.manager.github.GithubManager
import org.flaxo.rest.manager.gitplag.GitplagManager
import org.flaxo.rest.manager.response.Response
import org.flaxo.rest.manager.response.ResponseManager
import org.flaxo.rest.manager.travis.TravisManager
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

/**
 * Courses handling controller.
 */
// TODO 28.03.19: Move all controller methods business logic to CourseManager.
@RestController
@RequestMapping("/rest/course")
class CourseController(private val dataManager: DataManager,
                       private val responseManager: ResponseManager,
                       private val environmentManager: EnvironmentManager,
                       private val travisManager: TravisManager,
                       private val codacyManager: CodacyManager,
                       private val gitplagManager: GitplagManager,
                       private val githubManager: GithubManager,
                       private val courseValidations: Map<ExternalService, ValidationManager>,
                       private val courseManager: CourseManager
) {

    private val tasksPrefix = "task-"
    private val logger = LogManager.getLogger(CourseController::class.java)

    /**
     * Imports a course from an existing git repository or the [principal] by its [courseName].
     */
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun import(@RequestParam courseName: String,
               @RequestParam(required = false) description: String?,
               @RequestParam(required = false)  language: String?,
               @RequestParam(required = false)  testingLanguage: String?,
               @RequestParam(required = false)  testingFramework: String?,
               principal: Principal
    ): Response<CourseView> {
        logger.info("Trying to import course ${principal.name}/$courseName from an existing repository")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val githubId = user.githubId
                ?: return responseManager.githubIdNotFound(user.name)

        val githubToken = user.credentials.githubToken
                ?: return responseManager.githubTokenNotFound(user.name)

        return githubManager.with(githubToken)
                .getRepository(courseName)
                .takeIf { it.exists() }
                ?.let { repository ->
                    logger.info("Scanning for tasks of the importing course ${principal.name}/$courseName")
                    repository.addWebHook()
                    repository.branches()
                            .map { it.name }
                            .filter { it.startsWith(tasksPrefix) }
                            .let { tasksNames ->
                                dataManager.createCourse(
                                        courseName,
                                        description,
                                        false,
                                        language,
                                        testingLanguage,
                                        testingFramework,
                                        tasksNames,
                                        user
                                )
                            }
                }
                ?.let { responseManager.ok(it.view()) }
                ?: responseManager.bad("Github repository $githubId/$courseName doesn't exist")
    }

    /**
     * Creates a course and related git repository.
     *
     * @param courseName Name of the course and related git repository.
     * @param description Optional course description.
     * @param language Language of the course.
     * @param testingLanguage Language used for tests.
     * @param testingFramework Testing framework which is used for testing.
     * @param numberOfTasks Number of tasks in the course and number of tasks branches
     * in the git repository.
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun createCourse(@RequestParam courseName: String,
                     @RequestParam(required = false) description: String?,
                     @RequestParam(required = false)  language: String?,
                     @RequestParam(required = false)  testingLanguage: String?,
                     @RequestParam(required = false)  testingFramework: String?,
                     @RequestParam(required = false)  private: Boolean = false,
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
                .createRepository(courseName, private).apply {
                    createBranch("prerequisites")
                            .also { branch -> environment.files().forEach { branch.commit(it) } }
                            .createSubBranches(numberOfTasks, tasksPrefix)
                    addWebHook()
                }

        logger.info("Creating course ${principal.name}/$courseName in database")

        val course = dataManager.createCourse(
                courseName = courseName,
                description = description,
                private = private,
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
     * Updates course settings.
     *
     * @param id Course id to update settings for.
     * @param settings Updated course settings.
     */
    @PutMapping("/{id}/settings")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun updateSettings(@PathVariable id: Long,
                       @RequestBody settings: CourseSettings,
                       principal: Principal
    ): Response<CourseView> = responseManager.ok(courseManager.updateSettings(principal.name, id, settings))

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
                    it.credentials.githubToken ?: return responseManager.githubTokenNotFound(it.name)
                    it.githubId ?: return responseManager.githubIdNotFound(it.name)
                }
                ?: return responseManager.userNotFound(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(principal.name, courseName)

        val activatedServices = courseValidations
                .mapNotNull { (serviceType, service) ->
                    Try { service.activate(course) }
                            .map { serviceType }
                            .getOrElse {
                                logger.info("$serviceType activation went bad for ${user.name}/$courseName " +
                                        "course due to: ${it.stringStackTrace()}")
                                null
                            }
                }
                .toSet()

        logger.info("Changing course ${user.name}/$courseName status to running " +
                "with activated services: $activatedServices")

        val activatedCourse = dataManager.updateCourse(course.copy(
                state = course.state.copy(
                        lifecycle = CourseLifecycle.RUNNING,
                        activatedServices = activatedServices
                )
        ))

        logger.info("Course ${user.name}/$courseName has been successfully composed")

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
                        "${course.friendlyId} course")
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
            return responseManager.bad("Course ${course.friendlyId} is not started yet")
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
                        "${course.friendlyId} course")
    }

    /**
     * Activates gitplag validations for specified [courseName].
     *
     * Course should be started.
     *
     * @param courseName Course name to activate gitplag for.
     */
    @PostMapping("/activate/gitplag")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun activateGitplag(@RequestParam courseName: String,
                        principal: Principal
    ): Response<CourseView> {
        logger.info("Trying to activate gitplag for course ${principal.name}/$courseName")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(principal.name, courseName)

        if (course.state.lifecycle != CourseLifecycle.RUNNING) {
            return responseManager.bad("Course ${course.friendlyId} is not started yet")
        }

        course.state
                .activatedServices
                .takeUnless { it.contains(ExternalService.GITPLAG) }
                ?.let {
                    try {
                        gitplagManager.activate(course)

                        return dataManager
                                .updateCourse(course.copy(
                                        state =
                                        course.state.copy(
                                                activatedServices =
                                                course.state.activatedServices + ExternalService.GITPLAG
                                        )
                                ))
                                .let { responseManager.ok(it.view()) }
                    } catch (e: Exception) {
                        logger.info("Gitplag activation failed due to: $e")
                        return responseManager.bad(e.toString())
                    }
                }
                ?: return responseManager.bad("Gitplag is already integrated with " +
                        "${course.friendlyId} course")
    }

    /**
     * Synchronize course solutions and validations.
     */
    @PostMapping("/sync")
    fun synchronize(@RequestParam courseName: String,
                    principal: Principal
    ): Response<CourseStatisticsView> {
        logger.info("Syncing ${principal.name}/$courseName course")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val githubToken = user.credentials.githubToken
                ?: return responseManager.githubTokenNotFound(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(principal.name, courseName)

        logger.info("Upserting missing solutions and commits of ${course.friendlyId} course")

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
                ?: return responseManager.bad("Course ${courseWithNewSolutions.friendlyId} " +
                        "is not running to be synchronized")

        logger.info("Course validations were synchronized for ${courseWithNewSolutions.friendlyId}")

        val courseWithRefreshedValidations = dataManager.getCourse(courseWithNewSolutions.name, user)
                ?: return responseManager.courseNotFound(principal.name, courseWithNewSolutions.name)

        return responseManager.ok(CourseStatisticsView(courseWithRefreshedValidations.tasks.views()))
    }
}
