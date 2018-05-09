package org.flaxo.rest.service.travis

import io.vavr.kotlin.Try
import org.apache.logging.log4j.LogManager
import org.flaxo.cmd.CmdExecutor
import org.flaxo.core.of
import org.flaxo.core.repeatUntil
import org.flaxo.core.stringStackTrace
import org.flaxo.model.DataService
import org.flaxo.model.IntegratedService
import org.flaxo.model.data.Course
import org.flaxo.model.data.User
import org.flaxo.travis.SimpleTravis
import org.flaxo.travis.Travis
import org.flaxo.travis.TravisClient
import org.flaxo.travis.TravisException
import org.flaxo.travis.TravisUser
import org.flaxo.travis.build.TravisBuild
import org.flaxo.travis.parseTravisWebHook
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.io.Reader
import java.util.concurrent.TimeUnit

/**
 * Travis service basic implementation.
 */
open class TravisSimpleService(private val client: TravisClient,
                               private val dataService: DataService
) : TravisService {

    private val logger = LogManager.getLogger(TravisSimpleService::class.java)

    override fun retrieveTravisToken(githubUsername: String, githubToken: String): String {
        CmdExecutor.execute("travis", "login",
                "-u", githubUsername,
                "-g", githubToken)

        return CmdExecutor.execute("travis", "token")
                .first().split(" ").last()
    }

    override fun travis(travisToken: String): Travis =
            SimpleTravis(client, travisToken)

    override fun parsePayload(reader: Reader): TravisBuild? =
            parseTravisWebHook(reader)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun activateTravis(user: User,
                                course: Course,
                                githubToken: String,
                                githubUserId: String
    ) {
        logger.info("Initialising travis client for ${user.nickname} user")

        val travisToken = retrieveTravisToken(user, githubUserId, githubToken)

        val travis = travis(travisToken)

        logger.info("Retrieving travis user for ${user.nickname} user")

        val travisUser: TravisUser = travis.getUser()
                .getOrElseThrow { errorBody ->
                    TravisException("Travis user retrieving failed for ${user.nickname} " +
                            "due to: ${errorBody.string()}")
                }

        logger.info("Trigger travis user with id ${travisUser.id} sync for ${user.nickname} user")

        Try {
            repeatUntil("Travis synchronisation started",
                    initDelay = 15,
                    attemptsLimit = 5
            ) {
                travis.sync(travisUser.id) == null
            }
        }.onFailure { error ->
            throw TravisException("Travis user ${travisUser.id} sync hasn't started due to: ${error.stringStackTrace()}")
        }

        logger.info("Trying to ensure that current user's travis synchronisation has finished")

        repeatUntil("Travis synchronisation finishes",
                initDelay = 15
        ) {
            travis.getUser()
                    .getOrElseThrow { errorBody ->
                        TravisException("Travis user ${travisUser.id} retrieving went bad due to: ${errorBody.string()}")
                    }
                    .let { !it.isSyncing }
        }

        logger.info("Activating git repository of the course ${user.nickname}/${course.name} for travis CI")

        travis.activate(githubUserId, course.name)
                .getOrElseThrow { errorBody ->
                    TravisException("Travis activation of $githubUserId/${course.name} " +
                            "repository went bad due to: ${errorBody.string()}")
                }
    }

    private fun retrieveTravisToken(user: User,
                                    githubUserId: String,
                                    githubToken: String
    ): String = retrieveUserWithTravisToken(user, githubUserId, githubToken)
            .credentials
            .travisToken
            ?: throw TravisException("Travis token wasn't found for ${user.nickname}.")

    private fun retrieveUserWithTravisToken(user: User,
                                            githubUserId: String,
                                            githubToken: String
    ): User = user
            .takeUnless { it.credentials.travisToken.isNullOrBlank() }
            ?: dataService.addToken(
                    user.nickname,
                    IntegratedService.TRAVIS,
                    retrieveTravisToken(githubUserId, githubToken)
            )

}
