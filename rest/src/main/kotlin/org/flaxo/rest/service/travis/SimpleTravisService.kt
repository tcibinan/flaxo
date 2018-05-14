package org.flaxo.rest.service.travis

import org.apache.logging.log4j.LogManager
import org.flaxo.cmd.CmdExecutor
import org.flaxo.core.of
import org.flaxo.core.repeatUntil
import org.flaxo.model.DataService
import org.flaxo.model.IntegratedService
import org.flaxo.model.ModelException
import org.flaxo.model.data.Course
import org.flaxo.model.data.User
import org.flaxo.travis.retrofit.RetrofitTravisImpl
import org.flaxo.travis.Travis
import org.flaxo.travis.retrofit.TravisClient
import org.flaxo.travis.TravisException
import org.flaxo.travis.TravisUser
import org.flaxo.travis.TravisBuild
import org.flaxo.travis.TravisBuildStatus
import org.flaxo.travis.TravisBuildType
import org.flaxo.travis.parseTravisWebHook
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.io.Reader
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * Travis service basic implementation.
 */
open class SimpleTravisService(private val client: TravisClient,
                               private val dataService: DataService
) : TravisService {

    private val logger = LogManager.getLogger(SimpleTravisService::class.java)

    override fun retrieveTravisToken(githubUsername: String, githubToken: String): String {
        CmdExecutor.execute("travis", "login",
                "-u", githubUsername,
                "-g", githubToken)

        return CmdExecutor.execute("travis", "token")
                .first().split(" ").last()
    }

    override fun travis(travisToken: String): Travis =
            RetrofitTravisImpl(client, travisToken)

    override fun parsePayload(reader: Reader): TravisBuild? =
            parseTravisWebHook(reader)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun activate(course: Course) {
        val user = course.user

        val githubToken = user.credentials.githubToken
                ?: throw TravisException("Travis validation can't be activated because ${user.nickname} user" +
                        "doesn't have github token")

        val githubId = user.githubId
                ?: throw TravisException("Travis validation can't be activated because ${user.nickname} user" +
                        "doesn't have github id")

        logger.info("Initialising travis client for ${user.nickname} user")

        val travisToken = user.credentials.travisToken
                ?: retrieveTravisToken(githubId, githubToken)
                        .also {
                            logger.info("Adding newely retrieved travis token to ${user.nickname} user")
                            dataService.addToken(user.nickname, IntegratedService.TRAVIS, it)
                            // sleep is necessary because after travis token retrieving
                            // travis synchronisation is scheduled on travis-ci.org.
                            Thread.sleep(10 of TimeUnit.SECONDS)
                        }

        val travis = travis(travisToken)

        logger.info("Retrieving travis user for ${user.nickname} user")

        val travisUser: TravisUser = retrieveTravisUser(travis, user)

        logger.info("Triggering ${user.nickname} user travis synchronisation")

        // Delay is needed to prevent travis synchronizations overlap
        performAfter(60 of TimeUnit.SECONDS) {
            travis.sync(travisUser.id)
                    ?.also { errorBody ->
                        throw TravisException("Travis user ${travisUser.id} " +
                                "sync hasn't started due to: ${errorBody.string()}")
                    }
        }

        logger.info("Ensuring that ${user.nickname} user travis synchronisation has finished")

        repeatUntil("Travis synchronisation finishes") {
            retrieveTravisUser(travis, user)
                    .let { !it.isSyncing }
        }

        logger.info("Ensuring that ${user.nickname} user has ${course.name} travis repository")

        repeatUntil("Travis repository appears after synchronization") {
            travis.getRepository(githubId, course.name)
                    .getOrElseThrow { errorBody ->
                        TravisException("Travis user retrieving failed for ${user.nickname}" +
                                " due to: ${errorBody.string()}")
                    }
                    .let { true }
        }

        logger.info("Activating travis repository of the course ${user.nickname}/${course.name}")

        travis.activate(githubId, course.name)
                .getOrElseThrow { errorBody ->
                    TravisException("Travis activation of $githubId/${course.name} " +
                            "repository went bad due to: ${errorBody.string()}")
                }
    }

    @Transactional
    override fun deactivate(course: Course) {
        val user = course.user

        val githubId = user.githubId
                ?: throw ModelException("Github id for ${user.nickname} user was not found")

        user.credentials
                .travisToken
                ?.also {
                    logger.info("Deactivating travis for ${user.nickname}/${course.name} course")

                    travis(it)
                            .deactivate(githubId, course.name)
                            .getOrElseThrow { errorBody ->
                                TravisException("Travis deactivation of $githubId/${course.name}" +
                                        "repository went bad due to: ${errorBody.string()}")
                            }

                    logger.info("Travis deactivation for ${user.nickname}/${course.name} course " +
                            "has finished successfully")
                }
                ?.also {
                    logger.info("Removing travis from activated services of ${user.nickname}/${course.name} course")

                    dataService.updateCourse(course.copy(
                            state = course.state.copy(
                                    activatedServices = course.state.activatedServices - IntegratedService.TRAVIS
                            )
                    ))
                }
                ?: logger.info("Travis token wasn't found for ${user.nickname} course " +
                        "so no travis repository is deactivated")
    }

    @Transactional
    override fun refresh(course: Course) {
        val user = course.user

        val githubId = user.githubId
                ?: throw ModelException("Github id for ${user.nickname} user was not found")

        val travisToken = user.credentials.travisToken
                ?: throw ModelException("Travis token is not specified for ${user.nickname}")

        logger.info("Travis build results refreshing is started for ${user.nickname}/${course.name} course")

        course.tasks
                .flatMap { it.solutions }
                .forEach { solution ->
                    solution.commits
                            .lastOrNull()
                            ?.let { commit ->
                                travis(travisToken)
                                        .getBuilds(
                                                userName = githubId,
                                                repositoryName = course.name,
                                                eventType = TravisBuildType.PULL_REQUEST
                                        )
                                        .getOrElseThrow { errorBody ->
                                            TravisException("Travis builds retrieving failed due to: " +
                                                    errorBody.string())
                                        }
                                        .filter { it.commitSha == commit.sha }
                                        .filter {
                                            it.buildStatus in setOf(
                                                    TravisBuildStatus.SUCCEED,
                                                    TravisBuildStatus.FAILED
                                            )
                                        }
                                        .filter { it.finishedAt != null }
                                        .sortedBy { it.finishedAt }
                                        .lastOrNull()
                            }
                            ?.also { latestBuild ->
                                val latestBuildReportDate = solution.buildReports
                                        .lastOrNull()
                                        ?.date
                                        ?: LocalDateTime.MIN

                                latestBuild.takeIf {
                                    it.finishedAt ?: LocalDateTime.MIN > latestBuildReportDate
                                }
                            }
                            ?.also {
                                logger.info(
                                        "Updating ${solution.student.nickname} student build report " +
                                                "for ${solution.task.branch} branch " +
                                                "of ${user.nickname}/${course.name} course"
                                )
                                when (it.buildStatus) {
                                    TravisBuildStatus.SUCCEED -> dataService.addBuildReport(
                                            solution,
                                            succeed = true,
                                            date = it.finishedAt ?: LocalDateTime.MIN
                                    )
                                    TravisBuildStatus.FAILED -> dataService.addBuildReport(
                                            solution,
                                            succeed = false,
                                            date = it.finishedAt ?: LocalDateTime.MIN
                                    )
                                    else -> logger.info(
                                            "Commit ${it.commitSha} will be ignored during the current refreshing."
                                    )
                                }
                            }
                }

        logger.info("Travis build results were refreshed for ${user.nickname}/${course.name} course")
    }

    private fun retrieveTravisUser(travis: Travis,
                                   user: User
    ): TravisUser =
            travis.getUser()
                    .getOrElseThrow { errorBody ->
                        TravisException("Travis user retrieving failed for ${user.nickname}" +
                                " due to: ${errorBody.string()}")
                    }

}

private fun performAfter(millis: Long, block: () -> Unit) {
    Thread.sleep(millis)
    block()
}