package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.core.DataService
import com.tcibinan.flaxo.core.EntityAlreadyExistsException
import com.tcibinan.flaxo.rest.api.ServerAnswer.*
import com.tcibinan.flaxo.rest.services.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/rest")
class ModelController @Autowired constructor(
        val dataService: DataService,
        val messageService: MessageService
) {

    @PostMapping("/register")
    fun register(@RequestParam("nickname") nickname: String, @RequestParam("password") password: String): Response {
        return try {
            dataService.addUser(nickname, password)
            response(USER_CREATED, messageService.get("model.user.success.created", nickname))
        } catch (e: EntityAlreadyExistsException) {
            response(USER_ALREADY_EXISTS, messageService.get("model.user.error.already.exists", e.entity))
        } catch (e: Throwable) {
            response(SERVER_ERROR, e.message)
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
        val user = dataService.getUser(principal.name)
        user ?: throw Exception("Could not find user with ${principal.name} nickname")
        user.credentials.githubToken ?: return response(
                NO_GITHUB_KEY,
                messageService.get("operation.need.github.key")
        )

        val course = dataService.createCourse(courseName, language, testLanguage, testingFramework, numberOfTasks, user)
        // TODO: 08/12/17 Create github repository

        return response(
                COURSE_CREATED,
                messageService.get("course.success.created", courseName),
                course
        )
    }
}