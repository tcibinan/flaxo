package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.core.DataService
import com.tcibinan.flaxo.rest.api.ServerAnswer.*
import com.tcibinan.flaxo.rest.services.MessageService
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
@RequestMapping("/user")
class UserController @Autowired constructor(
        val dataService: DataService,
        val messageService: MessageService
) {

    @GetMapping("/{nickname}/allCourses")
    @PreAuthorize("hasAuthority('USER')")
    fun allCourses(@PathVariable nickname: String, principal: Principal): Response {
        return if (principal.name == nickname) {
            response(
                    COURSES_LIST,
                    messageService.get("user.success.all.courses"),
                    dataService.getCourses(nickname)
            )
        } else {
            response(
                    ANOTHER_USER_DATA,
                    messageService.get("user.error.get.others.courses", principal.name, nickname)
            )
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