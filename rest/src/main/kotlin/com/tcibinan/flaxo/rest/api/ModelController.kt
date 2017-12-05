package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.core.DataService
import com.tcibinan.flaxo.core.EntityAlreadyExistsException
import com.tcibinan.flaxo.rest.api.ServerAnswer.*
import com.tcibinan.flaxo.rest.services.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/model")
class ModelController @Autowired constructor(val dataService: DataService) {

    @Autowired lateinit var messageService: MessageService

    @PostMapping("/register")
    fun register(@RequestParam("nickname") nickname: String, @RequestParam("password") password: String): Response {
        return try {
            dataService.addUser(nickname, password)
            response(USER_CREATED,messageService.get("model.user.success.created", nickname))
        } catch (e: EntityAlreadyExistsException) {
            response(USER_ALREADY_EXISTS, messageService.get("model.user.error.already.exists", e.entity))
        } catch (e: Throwable) {
            response(SERVER_ERROR)
        }
    }
}