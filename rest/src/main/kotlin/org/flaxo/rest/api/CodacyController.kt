package org.flaxo.rest.api

import org.apache.logging.log4j.LogManager
import org.flaxo.model.DataService
import org.flaxo.model.IntegratedService
import org.flaxo.rest.service.response.ResponseService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

/**
 * Codacy integration configuration.
 */
@RestController
@RequestMapping("/rest/codacy")
class CodacyController(private val dataService: DataService,
                       private val responseService: ResponseService
) {

    private val logger = LogManager.getLogger(CodacyController::class.java)

    @PutMapping("/token")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun putCodacyToken(@RequestParam token: String,
                       principal: Principal
    ): ResponseEntity<Any> {
        logger.info("Putting codacy token for ${principal.name}")

        val user = dataService.getUser(principal.name)
                ?: return responseService.userNotFound(principal.name)

        if (token.isBlank()) {
            logger.error("Given codacy token for ${principal.name} is invalid")
            return responseService.bad("Given codacy token is invalid")
        }

        dataService.addToken(user.nickname, IntegratedService.CODACY, token)

        logger.info("Codacy token was added for ${principal.name}")
        return responseService.ok()
    }
}