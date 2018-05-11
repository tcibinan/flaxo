package org.flaxo.rest.api

import org.flaxo.rest.service.response.ResponseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Flaxo test controller.
 */
@RestController
@RequestMapping("/rest")
class FlaxoController @Autowired constructor(
        val responseService: ResponseService
) {

    /**
     * Shows welcome message.
     */
    @GetMapping("/")
    fun index() = responseService.ok("Welcome to flaxo")

    /**
     * Echoes the given message.
     *
     * An example of security usage.
     */
    @GetMapping("/echo")
    @PreAuthorize("hasAuthority('USER')")
    fun echo(@RequestParam("message") message: String) =
            responseService.ok(message)

}