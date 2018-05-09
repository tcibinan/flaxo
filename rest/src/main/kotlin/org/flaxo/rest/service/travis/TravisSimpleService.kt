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

        val travisToken = user.credentials.travisToken
                ?: retrieveTravisToken(githubUserId, githubToken)
                        .also { dataService.addToken(user.nickname, IntegratedService.TRAVIS, it) }

        val travis = travis(travisToken)

        logger.info("Retrieving travis user for ${user.nickname} user")

        val travisUser: TravisUser = retrieveTravisUser(travis, user)

        travis.getUser()
                .getOrElseThrow { errorBody ->
                    TravisException("Travis user ${travisUser.id} retrieving went bad due to: ${errorBody.string()}")
                }
                .takeIf { it.isSyncing }
                ?.also {
                    logger.info("Waiting for existing travis synchronisation to end")

                    repeatUntil("Travis synchronisation finishes") {
                        retrieveTravisUser(travis, user)
                                .let { !it.isSyncing }
                    }
                }

        logger.info("Triggering ${user.nickname} user new travis synchronisation")

        travis.sync(travisUser.id)
                ?.also { errorBody ->
                    throw TravisException("Travis user ${travisUser.id} " +
                            "sync hasn't started due to: ${errorBody.string()}")
                }

        logger.info("Ensuring that ${user.nickname} user travis synchronisation has finished")

        repeatUntil("Travis synchronisation finishes") {
            retrieveTravisUser(travis, user)
                    .let { !it.isSyncing }
        }

        logger.info("Activating git repository of the course ${user.nickname}/${course.name} for travis CI")

        travis.activate(githubUserId, course.name)
                .getOrElseThrow { errorBody ->
                    TravisException("Travis activation of $githubUserId/${course.name} " +
                            "repository went bad due to: ${errorBody.string()}")
                }
    }

    private fun retrieveTravisUser(travis: Travis, user: User): TravisUser =
            travis.getUser()
                    .getOrElseThrow { errorBody ->
                        TravisException("Travis user retrieving failed for ${user.nickname}" +
                                " due to: ${errorBody.string()}")
                    }

}
