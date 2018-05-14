package org.flaxo.rest.service.codacy

import org.apache.logging.log4j.LogManager
import org.flaxo.codacy.Codacy
import org.flaxo.codacy.CodacyClient
import org.flaxo.codacy.CodacyException
import org.flaxo.codacy.SimpleCodacy
import org.flaxo.core.repeatUntil
import org.flaxo.model.DataService
import org.flaxo.model.IntegratedService
import org.flaxo.model.ModelException
import org.flaxo.model.data.Course
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * Codacy service basic implementation.
 */
open class SimpleCodacyService(private val client: CodacyClient,
                               private val dataService: DataService
) : CodacyService {

    private val logger = LogManager.getLogger(SimpleCodacyService::class.java)

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
            val errorBody = codacy.createProject(
                    course.name,
                    "git://github.com/$githubId/${course.name}.git"
            )

            if (errorBody != null)
                throw CodacyException("Codacy project was not created due to: ${errorBody.string()}")
            else true
        }
    }

    @Transactional
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
                            ?.also { responseBody ->
                                throw CodacyException("Codacy project $githubId/${course.name} " +
                                        "deletion went bad due to: ${responseBody.string()}")
                            }

                    logger.info("Codacy deactivation for ${user.nickname}/${course.name} course " +
                            "has finished successfully")
                }
                ?.also {
                    logger.info("Removing codacy from activated services of ${user.nickname}/${course.name} course")

                    dataService.updateCourse(course.copy(
                            state = course.state.copy(
                                    activatedServices = course.state.activatedServices - IntegratedService.CODACY
                            )
                    ))
                }
                ?: logger.info("Codacy token wasn't found for ${user.nickname} " +
                        "so no codacy project is deleted")
    }

    @Transactional
    override fun refresh(course: Course) {
        val user = course.user

        val githubId = user.githubId
                ?: throw ModelException("Github id for ${user.nickname} user was not found")

        val codacyToken = user.credentials.codacyToken
                ?: throw CodacyException("Codacy token is not set for ${user.nickname} user so codacy " +
                        "service is not activated")

        logger.info("Codacy analyses results refreshing is started for ${user.nickname}/${course.name} course")

        course.tasks
                .flatMap { it.solutions }
                .forEach { solution ->
                    solution.commits
                            .lastOrNull()
                            ?.let { commit ->
                                codacy(githubId, codacyToken)
                                        .commitDetails(course.name, commit.sha)
                                        .getOrElseThrow { errorBody ->
                                            CodacyException("Codacy commit details retrieving failed due to: " +
                                                    errorBody.string())
                                        }
                                        .commit
                            }
                            ?.let { codacyCommit ->
                                val latestGrade = solution.codeStyleReports
                                        .lastOrNull()
                                        ?.grade
                                        ?: "No grade"

                                codacyCommit.takeIf { it.grade != latestGrade }
                            }
                            ?.also { codacyCommit ->
                                logger.info(
                                        "Updating ${solution.student.nickname} student code style report " +
                                                "for ${solution.task.branch} branch " +
                                                "of ${user.nickname}/${course.name} course"
                                )
                                dataService.addCodeStyleReport(
                                        solution,
                                        codeStyleGrade = codacyCommit.grade
                                )
                            }
                }

        logger.info("Codacy analyses results were refreshed for ${user.nickname}/${course.name} course")
    }

}