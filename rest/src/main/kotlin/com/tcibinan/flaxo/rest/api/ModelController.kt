package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.core.DataService
import com.tcibinan.flaxo.core.EntityAlreadyExistsException
import com.tcibinan.flaxo.core.env.languages.Language
import com.tcibinan.flaxo.rest.api.ServerAnswer.*
import com.tcibinan.flaxo.rest.services.GitService
import com.tcibinan.flaxo.rest.services.RepositoryEnvironmentService
import com.tcibinan.flaxo.rest.services.Response
import com.tcibinan.flaxo.rest.services.ResponseService
import com.tcibinan.flaxo.rest.services.createCourse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/rest")
class ModelController @Autowired constructor(
        val dataService: DataService,
        val responseService: ResponseService,
        val environmentService: RepositoryEnvironmentService,
        val gitService: GitService,
        val supportedLanguages: Map<String, Language>
) {

    @PostMapping("/register")
    fun register(
            @RequestParam("nickname") nickname: String,
            @RequestParam("password") password: String
    ): Response {
        return try {
            dataService.addUser(nickname, password)
            responseService.response(USER_CREATED, args = *arrayOf(nickname))
        } catch (e: EntityAlreadyExistsException) {
            responseService.response(USER_ALREADY_EXISTS, args = *arrayOf(e.entity))
        } catch (e: Throwable) {
            responseService.response(SERVER_ERROR, e.message)
        }
    }

    @PostMapping("/createCourse")
    @PreAuthorize("hasAuthority('USER')")
    fun createCourse(
            @RequestParam courseName: String,
            @RequestParam language: String,
            @RequestParam testLanguage: String,
            @RequestParam testingFramework: String,
            @RequestParam numberOfTasks: Int,
            principal: Principal
    ): Response {
        val user = dataService.getUser(principal.name) ?:
                throw Exception("Could not find user with ${principal.name} nickname")

        val githubToken = user.credentials.githubToken ?:
                return responseService.response(NO_GITHUB_KEY)

        val course = dataService.createCourse(courseName, language, testLanguage, testingFramework, numberOfTasks, user)

        val environment = environmentService.produceEnvironment(language, testLanguage, testingFramework)

        gitService.with(githubToken)
                .createCourse(courseName, environment, numberOfTasks)

        return responseService.response(COURSE_CREATED, args = *arrayOf(courseName), payload = course)
    }

    @GetMapping("/supportedLanguages")
    fun supportedLanguages(): Response =
            responseService.response(SUPPORTED_LANGUAGES, payload = supportedLanguages.flatten())
}

private fun Map<String, Language>.flatten(): List<Any> =
        map { (name, language) ->
            object {
                val name = name
                val compatibleTestingLanguages = language.compatibleTestingLanguages().map { it.name() }
                val compatibleTestingFrameworks = language.compatibleTestingFrameworks().map { it.name() }
            }
        }
