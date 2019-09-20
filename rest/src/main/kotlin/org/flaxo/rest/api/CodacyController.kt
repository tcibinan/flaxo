package org.flaxo.rest.api

import org.apache.logging.log4j.LogManager
import org.flaxo.common.data.ExternalService
import org.flaxo.model.DataManager
import org.flaxo.model.UserView
import org.flaxo.rest.manager.response.Response
import org.flaxo.rest.manager.response.ResponseManager
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

/**
 * Codacy controller.
 */
@RestController
@RequestMapping("/rest/codacy")
class CodacyController(private val dataManager: DataManager,
                       private val responseManager: ResponseManager
) {

    private val logger = LogManager.getLogger(CodacyController::class.java)

    /**
     * Adds a codacy token to [principal] credentials.
     */
    @PutMapping("/token")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun putToken(@RequestParam token: String,
                 principal: Principal
    ): Response<UserView> {
        logger.info("Putting codacy token for ${principal.name}")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        if (token.isBlank()) {
            logger.error("Given codacy token for ${principal.name} is invalid")
            return responseManager.bad("Given codacy token is invalid")
        }

        val updatedUser = dataManager.addToken(user.name, ExternalService.CODACY, token)
        logger.info("Codacy token was added for ${principal.name}")
        return responseManager.ok(updatedUser.view())
    }
}
