package org.flaxo.rest.api

import org.flaxo.model.DataManager
import org.flaxo.rest.manager.github.GithubManager
import org.flaxo.rest.manager.travis.TravisManager
import org.flaxo.travis.TravisException
import org.flaxo.travis.TravisBuildStatus
import org.flaxo.travis.TravisBuild
import org.flaxo.travis.TravisPullRequestBuild
import org.apache.logging.log4j.LogManager
import org.flaxo.common.ExternalService
import org.flaxo.rest.manager.response.Response
import org.flaxo.rest.manager.response.ResponseManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.Reader
import java.security.Principal
import javax.servlet.http.HttpServletRequest

/**
 * Travis integration controller.
 */
@RestController
@RequestMapping("/rest/travis")
class TravisController @Autowired constructor(private val travisManager: TravisManager,
                                              private val dataManager: DataManager,
                                              private val githubManager: GithubManager,
                                              private val responseManager: ResponseManager
) {

    private val logger = LogManager.getLogger(TravisController::class.java)

    /**
     * Adds a travis token to [principal] credentials.
     */
    @PutMapping("/token")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun putToken(@RequestParam token: String,
                 principal: Principal
    ): Response<Unit> {
        logger.info("Putting travis token for ${principal.name} user")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        if (token.isBlank()) {
            logger.error("Given travis token for ${principal.name} is invalid")
            return responseManager.bad("Given travis token is invalid")
        }

        dataManager.addToken(user.nickname, ExternalService.TRAVIS, token)
        logger.info("Travis token was added for ${principal.name}")
        return responseManager.ok()
    }

    /**
     * Retrieves and handle travis build webhook.
     */
    @PostMapping("/hook")
    @Transactional
    fun travisWebHook(request: HttpServletRequest) {
        val payload: Reader = request.getParameter("payload").reader()
        val hook: TravisBuild = travisManager.parsePayload(payload)
                ?: throw UnsupportedOperationException("Unsupported travis web hook type")

        when (hook) {
            is TravisPullRequestBuild -> {
                val user = dataManager.getUserByGithubId(hook.repositoryOwner)
                        ?: throw TravisException("User with the required nickname ${hook.repositoryOwner} wasn't found.")

                val githubCredentials = user.credentials.githubToken
                        ?: throw TravisException("User ${user.nickname} doesn't have github credentials " +
                                "to get pull request information.")

                val course = dataManager.getCourse(hook.repositoryName, user)
                        ?: throw TravisException("Course with name ${hook.repositoryName} wasn't found " +
                                "for user ${user.nickname}.")

                val pullRequest = githubManager.with(githubCredentials)
                        .getRepository(course.name)
                        .getPullRequest(hook.pullRequestNumber)

                val student = course.students
                        .find { it.nickname == pullRequest.authorId }
                        ?: throw TravisException("Student ${pullRequest.authorId} wasn't found " +
                                "in course ${hook.repositoryOwner}/${hook.repositoryName}.")

                val solution = student.solutions
                        .find { it.task.branch == hook.branch }
                        ?: throw TravisException("Student task ${hook.branch} wasn't found for student ${student.nickname} " +
                                "in course ${hook.repositoryOwner}/${hook.repositoryName}.")

                when (hook.buildStatus) {
                    TravisBuildStatus.SUCCEED -> {
                        logger.info("Travis pull request successful build web hook received " +
                                "for ${hook.repositoryOwner}/${hook.repositoryName}.")

                        val buildReport = dataManager.addBuildReport(solution, succeed = true)

                        dataManager.updateSolution(solution.copy(
                                buildReports = solution.buildReports.plus(buildReport)
                        ))
                    }
                    TravisBuildStatus.FAILED -> {
                        logger.info("Travis pull request failed build web hook received " +
                                "for ${hook.repositoryOwner}/${hook.repositoryName}.")

                        val buildReport = dataManager.addBuildReport(solution, succeed = false)

                        dataManager.updateSolution(solution.copy(
                                buildReports = solution.buildReports.plus(buildReport)
                        ))
                    }
                    TravisBuildStatus.IN_PROGRESS -> {
                        logger.info("Travis pull request in progress build web hook received " +
                                "for ${hook.repositoryOwner}/${hook.repositoryName}.")

                        // ignore
                    }
                    else -> {
                        logger.info("Custom travis pull request web hook received " +
                                "for ${hook.repositoryOwner}/${hook.repositoryName}.")

                        // do nothing
                    }
                }
            }
            else -> {
                logger.warn("Custom travis web hook received from request $request.")

                //do nothing
            }
        }

    }
}
