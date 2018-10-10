package org.flaxo.rest.api

import org.flaxo.rest.manager.response.Response
import org.flaxo.rest.manager.response.ResponseManager
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
class FlaxoController(private val responseManager: ResponseManager) {

    /**
     * Shows welcome message.
     */
    @GetMapping
    fun index(): Response<String> = responseManager.ok("Welcome to flaxo")

    /**
     * Echoes the given message.
     *
     * An example of security usage.
     */
    @GetMapping("/echo")
    @PreAuthorize("hasAuthority('USER')")
    fun echo(@RequestParam("message") message: String): Response<String> = responseManager.ok(message)

}
