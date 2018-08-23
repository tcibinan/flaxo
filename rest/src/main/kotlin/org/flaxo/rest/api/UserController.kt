package org.flaxo.rest.api

import org.flaxo.model.DataService
import org.flaxo.model.EntityAlreadyExistsException
import org.flaxo.rest.service.response.ResponseService
import org.apache.logging.log4j.LogManager
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

/**
 * Users handling controller.
 */
@RestController
@RequestMapping("/rest/user")
class UserController(private val dataService: DataService,
                     private val responseService: ResponseService
) {

    private val logger = LogManager.getLogger(UserController::class.java)

    /**
     * Register user in the flaxo system.
     *
     * @param nickname Of the creating user.
     * @param password Of the creating user.
     */
    @PostMapping("/register")
    @Transactional
    fun register(@RequestParam nickname: String,
                 @RequestParam password: String
    ): ResponseEntity<Any> {
        logger.info("Trying to register user $nickname")

        return try {
            val user = dataService.addUser(nickname, password)

            logger.info("User $nickname was registered successfully")

            responseService.ok(user.view())
        } catch (e: EntityAlreadyExistsException) {
            logger.info("Trying to create user with $nickname nickname that is already registered")

            responseService.bad("User $nickname already exists")
        }
    }

    /**
     * Returns user account information.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    @Transactional(readOnly = true)
    fun user(principal: Principal): Any {
        logger.info("Trying to retrieve user ${principal.name}")

        val user = dataService.getUser(principal.name)
                ?: return responseService.userNotFound(principal.name)

        return responseService.ok(user.view())
    }

}