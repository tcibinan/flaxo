package org.flaxo.rest.api

import org.flaxo.model.DataManager
import org.flaxo.model.EntityAlreadyExistsException
import org.flaxo.rest.manager.response.ResponseManager
import org.apache.logging.log4j.LogManager
import org.flaxo.model.UserView
import org.flaxo.rest.manager.response.Response
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
class UserController(private val dataManager: DataManager,
                     private val responseManager: ResponseManager
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
    ): Response<UserView> {
        logger.info("Trying to register user $nickname")

        return try {
            val user = dataManager.addUser(nickname, password)

            logger.info("User $nickname was registered successfully")

            responseManager.ok(user.view())
        } catch (e: EntityAlreadyExistsException) {
            logger.info("Trying to create user with $nickname nickname that is already registered")

            responseManager.bad("User $nickname already exists")
        }
    }

    /**
     * Returns user account information.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    @Transactional(readOnly = true)
    fun user(principal: Principal): Response<UserView> {
        logger.info("Trying to retrieve user ${principal.name}")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        return responseManager.ok(user.view())
    }

}
