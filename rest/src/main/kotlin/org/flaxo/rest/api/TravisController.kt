package org.flaxo.rest.api

import org.flaxo.model.DataService
import org.flaxo.rest.service.git.GitService
import org.flaxo.rest.service.travis.TravisService
import org.flaxo.travis.TravisException
import org.flaxo.travis.build.BuildStatus
import org.flaxo.travis.build.TravisBuild
import org.flaxo.travis.build.TravisPullRequestBuild
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.io.Reader
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/rest/travis")
class TravisController @Autowired constructor(private val travisService: TravisService,
                                              private val dataService: DataService,
                                              private val gitService: GitService
) {

    private val logger = LogManager.getLogger(TravisController::class.java)

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
                        .getPullRequest(course.name, hook.number)

                val student = course.students
                        .find { it.nickname == pullRequest.authorId }
                        ?: throw TravisException("Student ${pullRequest.authorId} wasn't found " +
                                "in course ${hook.repositoryOwner}/${hook.repositoryName}.")

                val solution = student.solutions
                        .find { it.task.branch == hook.branch }
                        ?: throw TravisException("Student task ${hook.branch} wasn't found for student ${student.nickname} " +
                                "in course ${hook.repositoryOwner}/${hook.repositoryName}.")

                when (hook.status) {
                    BuildStatus.SUCCEED -> {
                        logger.info("Travis pull request successful build web hook received " +
                                "for ${hook.repositoryOwner}/${hook.repositoryName}.")

                        val buildReport = dataService.addBuildReport(solution, succeed = true)

                        dataService.updateSolution(solution.copy(buildReport = buildReport))
                    }
                    BuildStatus.FAILED -> {
                        logger.info("Travis pull request failed build web hook received " +
                                "for ${hook.repositoryOwner}/${hook.repositoryName}.")

                        val buildReport = dataService.addBuildReport(solution, succeed = false)

                        dataService.updateSolution(solution.copy(buildReport = buildReport))
                    }
                    BuildStatus.IN_PROGRESS -> {
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