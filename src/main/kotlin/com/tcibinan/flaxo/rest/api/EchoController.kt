package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.rest.model.Echo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class EchoController {

    @GetMapping("/echo")
    fun echo(@RequestParam(value = "message") message: String) = Echo(message)
}