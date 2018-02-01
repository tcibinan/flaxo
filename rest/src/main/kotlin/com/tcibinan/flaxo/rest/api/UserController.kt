package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.model.DataService
import com.tcibinan.flaxo.rest.api.ServerAnswer.*
import com.tcibinan.flaxo.rest.services.Response
import com.tcibinan.flaxo.rest.services.ResponseService
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
        val responseService: ResponseService
) {

    @GetMapping("user/{nickname}/allCourses")
    @PreAuthorize("hasAuthority('USER')")
    fun allCourses(@PathVariable nickname: String, principal: Principal): Response {
        return if (principal.name == nickname) {
            responseService.response(COURSES_LIST, payload = dataService.getCourses(nickname))
        } else {
            responseService.response(ANOTHER_USER_DATA, principal.name, nickname)
        }
    }

}