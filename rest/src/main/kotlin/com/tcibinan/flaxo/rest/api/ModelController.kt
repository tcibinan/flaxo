package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.core.DataService
import com.tcibinan.flaxo.core.EntityAlreadyExistsException
import com.tcibinan.flaxo.rest.api.ServerAnswer.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/model")
class ModelController @Autowired constructor(val dataService: DataService) {

    @PostMapping("/register")
    fun register(@RequestParam("nickname") nickname: String, @RequestParam("password") password: String): Response {
        return try {
            dataService.addUser(nickname, password)
            response(USER_CREATED,"user with '${nickname}' nickname was successfully created")
        } catch (e: EntityAlreadyExistsException) {
            response(USER_ALREADY_EXISTS, e.message)
        } catch (e: Throwable) {
            response(SERVER_ERROR)
        }
    }
}