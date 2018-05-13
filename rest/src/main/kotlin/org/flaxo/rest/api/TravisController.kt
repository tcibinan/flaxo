package org.flaxo.rest.api

import org.flaxo.model.DataService
import org.flaxo.rest.service.git.GitService
import org.flaxo.rest.service.travis.TravisService
import org.flaxo.travis.TravisException
import org.flaxo.travis.TravisBuildStatus
import org.flaxo.travis.TravisBuild
import org.flaxo.travis.TravisPullRequestBuild
import org.apache.logging.log4j.LogManager
import org.flaxo.model.IntegratedService
import org.flaxo.rest.service.response.ResponseService
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
class TravisController @Autowired constructor(private val travisService: TravisService,
                                              private val dataService: DataService,
                                              private val gitService: GitService,
                                              private val responseService: ResponseService
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
    ): ResponseEntity<Any> {
        logger.info("Putting travis token for ${principal.name} user")

        val user = dataService.getUser(principal.name)
                ?: return responseService.userNotFound(principal.name)

        if (token.isBlank()) {
            logger.error("Given travis token for ${principal.name} is invalid")
            return responseService.bad("Given travis token is invalid")
        }

        dataService.addToken(user.nickname, IntegratedService.TRAVIS, token)

        logger.info("Travis token was added for ${principal.name}")
        return responseService.ok()
    }

    /**
     * Retrieves and handle travis build webhook.
     */
    @PostMapping("/hook")
    @Transactional
    fun travisWebHook(request: HttpServletRequest) {
        val payload: Reader = request.getParameter("payload").reader()
        val hook: TravisBuild = travisService.parsePayload(payload)
                ?: throw UnsupportedOperationException("Unsupported travis web hook type")

        when (hook) {
            is TravisPullRequestBuild -> {
                val user = dataService.getUserByGithubId(hook.repositoryOwner)
                        ?: throw TravisException("User with the required nickname ${hook.repositoryOwner} wasn't found.")

                val githubCredentials = user.credentials.githubToken
                        ?: throw TravisException("User ${user.nickname} doesn't have github credentials " +
                                "to get pull request information.")

                val course = dataService.getCourse(hook.repositoryName, user)
                        ?: throw TravisException("Course with name ${hook.repositoryName} wasn't found " +
                                "for user ${user.nickname}.")

                val pullRequest = gitService.with(githubCredentials)
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

                        val buildReport = dataService.addBuildReport(solution, succeed = true)

                        dataService.updateSolution(solution.copy(
                                buildReports = solution.buildReports.plus(buildReport)
                        ))
                    }
                    TravisBuildStatus.FAILED -> {
                        logger.info("Travis pull request failed build web hook received " +
                                "for ${hook.repositoryOwner}/${hook.repositoryName}.")

                        val buildReport = dataService.addBuildReport(solution, succeed = false)

                        dataService.updateSolution(solution.copy(
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