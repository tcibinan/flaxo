package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.core.DataService
import com.tcibinan.flaxo.rest.api.ServerAnswer.*
import com.tcibinan.flaxo.rest.services.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/rest")
class UserController @Autowired constructor(
        val dataService: DataService,
        val messageService: MessageService
) {

    @GetMapping("user/{nickname}/allCourses")
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

}