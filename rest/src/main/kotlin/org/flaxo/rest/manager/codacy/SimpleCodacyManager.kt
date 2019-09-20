package org.flaxo.rest.manager.codacy

import arrow.core.Try
import arrow.core.getOrElse
import arrow.core.getOrHandle
import org.apache.logging.log4j.LogManager
import org.flaxo.codacy.Codacy
import org.flaxo.codacy.CodacyClient
import org.flaxo.codacy.CodacyException
import org.flaxo.codacy.SimpleCodacy
import org.flaxo.common.data.CodeStyleGrade
import org.flaxo.common.data.ExternalService
import org.flaxo.common.repeatUntil
import org.flaxo.common.stringStackTrace
import org.flaxo.model.DataManager
import org.flaxo.model.ModelException
import org.flaxo.model.data.Course
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * Codacy manager implementation.
 */
open class SimpleCodacyManager(private val client: CodacyClient,
                               private val dataManager: DataManager
) : CodacyManager {

    private val logger = LogManager.getLogger(SimpleCodacyManager::class.java)

    override fun codacy(githubId: String, codacyToken: String): Codacy =
            SimpleCodacy(githubId, codacyToken, client)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun activate(course: Course) {
        val user = course.user

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
            codacy
                    .createProject(
                            projectName = course.name,
                            repositoryUrl = "git://github.com/$githubId/${course.name}.git"
                    )
                    .map { true }
                    .getOrHandle { errorBody ->
                        throw CodacyException("Codacy project was not created due to: ${errorBody.string()}")
                    }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun deactivate(course: Course) {
        val user = course.user

        val githubId = user.githubId
                ?: throw ModelException("Github id for ${user.nickname} user was not found")

        user.credentials
                .codacyToken
                ?.also {
                    logger.info("Deactivating codacy for ${user.nickname}/${course.name} course")

                    codacy(githubId, it)
                            .deleteProject(course.name)
                            .getOrHandle { errorBody ->
                                throw CodacyException("Codacy project $githubId/${course.name} " +
                                        "deletion went bad due to: ${errorBody.string()}")
                            }

                    logger.info("Codacy deactivation for ${user.nickname}/${course.name} course " +
                            "has finished successfully")
                }
                ?.also {
                    logger.info("Removing codacy from activated services of ${user.nickname}/${course.name} course")

                    dataManager.updateCourse(course.copy(
                            state = course.state.copy(
                                    activatedServices = course.state.activatedServices - ExternalService.CODACY
                            )
                    ))
                }
                ?: logger.info("Codacy token wasn't found for ${user.nickname} " +
                        "so no codacy project is deleted")
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun refresh(course: Course) {
        val user = course.user

        val githubId = user.githubId
                ?: throw ModelException("Github id for ${user.nickname} user was not found")

        val codacyToken = user.credentials.codacyToken
                ?: throw CodacyException("Codacy token is not set for ${user.nickname} user so codacy " +
                        "service is not activated")

        logger.info("Codacy analyses results refreshing is started for ${user.nickname}/${course.name} course")

        val codacy = codacy(githubId, codacyToken)

        course.tasks
                .flatMap { it.solutions }
                .forEach { solution ->
                    solution.commits
                            .lastOrNull()
                            ?.let { commit ->
                                Try {
                                    codacy
                                            .commitDetails(course.name, commit.sha)
                                            .getOrHandle { errorBody ->
                                                throw CodacyException("Codacy commit details retrieving failed " +
                                                        "due to: ${errorBody.string()})")
                                            }
                                            .commit
                                }.getOrElse {
                                    logger.error("Codacy commit details retrieving failed " +
                                            "due to: ${it.stringStackTrace()}")
                                    null
                                }
                            }
                            ?.takeIf { it.grade.isNotBlank() }
                            ?.let { codacyCommit ->
                                val latestGrade = solution.codeStyleReports
                                        .lastOrNull()
                                        ?.grade
                                        ?: "No grade"

                                if (codacyCommit.grade != latestGrade) codacyCommit else null
                            }
                            ?.also { codacyCommit ->
                                logger.info(
                                        "Updating ${solution.student.name} student code style report " +
                                                "for ${solution.task.branch} branch " +
                                                "of ${user.nickname}/${course.name} course"
                                )
                                dataManager.addCodeStyleReport(
                                        solution,
                                        codeStyleGrade = CodeStyleGrade.valueOf(codacyCommit.grade)
                                )
                            }
                }

        logger.info("Codacy analyses results were refreshed for ${user.nickname}/${course.name} course")
    }

}