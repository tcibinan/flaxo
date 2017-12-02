package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.rest.api.ServerAnswer.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class FlaxoController {

    @GetMapping("/")
    fun index() = response(HELLO_WORLD, "Hello world!")

    @GetMapping("/echo")
    fun echo(@RequestParam("message") message: String) = response(ECHO, message)

}