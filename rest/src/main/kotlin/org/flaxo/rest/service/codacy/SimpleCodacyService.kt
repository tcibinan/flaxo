package org.flaxo.rest.service.codacy

import org.apache.logging.log4j.LogManager
import org.flaxo.codacy.Codacy
import org.flaxo.codacy.CodacyClient
import org.flaxo.codacy.CodacyException
import org.flaxo.codacy.SimpleCodacy
import org.flaxo.core.repeatUntil
import org.flaxo.model.data.Course
import org.flaxo.model.data.User
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * Codacy service basic implementation.
 */
open class SimpleCodacyService(private val client: CodacyClient
) : CodacyService {

    private val logger = LogManager.getLogger(SimpleCodacyService::class.java)

    override fun codacy(githubId: String, codacyToken: String): Codacy =
            SimpleCodacy(githubId, codacyToken, client)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun activateServiceFor(user: User,
                                    course: Course
    ) {
        val githubId = user.githubId
                ?: throw CodacyException("Codacy validations can't be activated because ${user.nickname} user" +
                        "doesn't have github id")

        val codacyToken = user.credentials.codacyToken
                ?: throw CodacyException("Codacy token is not set for ${user.nickname} user so codacy " +
                        "service is not activated")

        logger.info("Initialising codacy client for ${user.nickname} user")

        val codacy = codacy(githubId, codacyToken)

        logger.info("Creating codacy project ${course.name} for ${user.nickname} user")

        repeatUntil("Codacy project created",
                attemptsLimit = 5) {
            val errorBody = codacy.createProject(
                    course.name,
                    "git://github.com/$githubId/${course.name}.git"
            )

            if (errorBody != null)
                throw CodacyException("Codacy project was not created due to: ${errorBody.string()}")
            else true
        }
    }

}