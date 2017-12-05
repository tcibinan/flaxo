package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.rest.api.ServerAnswer.*
import com.tcibinan.flaxo.rest.services.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class FlaxoController {

    @Autowired lateinit var messageService: MessageService

    @GetMapping("/")
    fun index() = response(HELLO_WORLD, messageService.get("greeting"))

    @GetMapping("/echo")
    fun echo(@RequestParam("message") message: String) = response(ECHO, message)

}