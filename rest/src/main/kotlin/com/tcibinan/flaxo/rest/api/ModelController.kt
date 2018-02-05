package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.model.DataService
import com.tcibinan.flaxo.model.EntityAlreadyExistsException
import com.tcibinan.flaxo.core.language.Language
import com.tcibinan.flaxo.model.data.CourseStatus
import com.tcibinan.flaxo.model.data.StudentTask
import com.tcibinan.flaxo.rest.api.ServerAnswer.*
import com.tcibinan.flaxo.rest.service.git.GitService
import com.tcibinan.flaxo.rest.service.git.RepositoryEnvironmentService
import com.tcibinan.flaxo.rest.service.response.Response
import com.tcibinan.flaxo.rest.service.response.ResponseService
import com.tcibinan.flaxo.rest.service.git.createCourse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/rest")
class ModelController @Autowired constructor(
        private val dataService: DataService,
        private val responseService: ResponseService,
        private val environmentService: RepositoryEnvironmentService,
        private val gitService: GitService,
        private val supportedLanguages: Map<String, Language>
) {

    @PostMapping("/register")
    fun register(@RequestParam("nickname") nickname: String,
                 @RequestParam("password") password: String
    ): Response {
        return try {
            dataService.addUser(nickname, password)
            responseService.response(USER_CREATED, nickname)
        } catch (e: EntityAlreadyExistsException) {
            responseService.response(USER_ALREADY_EXISTS, e.entity)
        } catch (e: Throwable) {
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
        val user = dataService.getUser(principal.name) ?:
                return responseService.response(USER_NOT_FOUND, principal.name)

        val githubToken = user.credentials.githubToken ?:
                return responseService.response(NO_GITHUB_KEY)

        val course = dataService.createCourse(
                courseName,
                language,
                testLanguage,
                testingFramework,
                numberOfTasks,
                user
        )

        val environment = environmentService.produceEnvironment(
                language,
                testLanguage,
                testingFramework
        )

        gitService.with(githubToken)
                .createCourse(courseName, environment, numberOfTasks)
                .addWebHook(courseName)

        return responseService.response(COURSE_CREATED, courseName, payload = course)
    }

    @PostMapping("/deleteCourse")
    @PreAuthorize("hasAuthority('USER')")
    fun deleteCourse(@RequestParam courseName: String,
                     principal: Principal
    ): Response {
        val user = dataService.getUser(principal.name) ?:
                return responseService.response(USER_NOT_FOUND, principal.name)

        val githubToken = user.credentials.githubToken ?:
                return responseService.response(NO_GITHUB_KEY)

        dataService.deleteCourse(courseName, user)

        gitService.with(githubToken).deleteRepository(courseName)

        return responseService.response(COURSE_DELETED, courseName)
    }

    @PostMapping("/composeCourse")
    @PreAuthorize("hasAuhority('USER')")
    fun composeCourse(@RequestParam courseName: String,
                      principal: Principal
    ) : Response {
        val user = dataService.getUser(principal.name) ?:
                return responseService.response(USER_NOT_FOUND, principal.name)

        val course = dataService.getCourse(courseName, user) ?:
                return responseService.response(COURSE_NOT_FOUND, principal.name, courseName)

        dataService.updateCourse(course.copy(status = CourseStatus.RUNNING))

        return responseService.response(COURSE_COMPOSED, courseName)
    }

    @GetMapping("/{owner}/{course}/statistics")
    @PreAuthorize("hasAuthority('USER')")
    fun getCourseStatistics(@PathVariable("owner") ownerNickname: String,
                            @PathVariable("course") courseName: String
    ): Response {
        val user = dataService.getUser(ownerNickname) ?:
                return responseService.response(USER_NOT_FOUND, ownerNickname)

        val course = dataService.getCourse(courseName, user) ?:
                return responseService.response(COURSE_NOT_FOUND, ownerNickname, courseName)

        return responseService.response(
                STUDENTS_STATISTICS,
                payload = course.students
                        .map { it.nickname to it.studentTasks.reports() }
                        .toMap()
        )
    }

    @GetMapping("/supportedLanguages")
    fun supportedLanguages(): Response =
            responseService.response(SUPPORTED_LANGUAGES, payload = supportedLanguages.flatten())

    private fun Collection<StudentTask>.reports(): List<Any> =
            map {
                object {
                    val totalPoints = it.points
                }
            }

    private fun Map<String, Language>.flatten(): List<Any> =
            map { (name, language) ->
                object {
                    val name = name
                    val compatibleTestingLanguages = language.compatibleTestingLanguages().map { it.name() }
                    val compatibleTestingFrameworks = language.compatibleTestingFrameworks().map { it.name() }
                }
            }
}