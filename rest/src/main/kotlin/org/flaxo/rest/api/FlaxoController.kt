package org.flaxo.rest.api

import org.flaxo.rest.service.response.ResponseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest")
class FlaxoController @Autowired constructor(
        val responseService: ResponseService
) {

    @GetMapping("/")
    fun index() = responseService.ok("Welcome to flaxo")

    @GetMapping("/echo")
    @PreAuthorize("hasAuthority('USER')")
    fun echo(@RequestParam("message") message: String) =
            responseService.ok(message)

}