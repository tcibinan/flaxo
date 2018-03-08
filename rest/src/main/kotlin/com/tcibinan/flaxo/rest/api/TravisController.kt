package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.model.DataService
import com.tcibinan.flaxo.model.data.StudentTask
import com.tcibinan.flaxo.rest.service.git.GitService
import com.tcibinan.flaxo.rest.service.travis.TravisService
import com.tcibinan.flaxo.travis.build.BuildStatus
import com.tcibinan.flaxo.travis.build.TravisBuild
import com.tcibinan.flaxo.travis.build.TravisPullRequestBuild
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
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
    fun travisWebHook(request: HttpServletRequest) {
        val payload: Reader = request.getParameter("payload").reader()
        val build: TravisBuild = travisService.parsePayload(payload)
                ?: throw UnsupportedOperationException("Unsupported travis web hook type")

        when (build) {
            is TravisPullRequestBuild ->
                when (build.status) {
                    BuildStatus.SUCCEED -> {
                        logger.info("Travis pull request successful build web hook received " +
                                "for ${build.repositoryOwner}/${build.repositoryName}.")

                        dataService.updateStudentTask(getStudentTaskBy(build).with(
                                anyBuilds = true,
                                buildSucceed = true
                        ))
                    }
                    BuildStatus.FAILED -> {
                        logger.info("Travis pull request failed build web hook received " +
                                "for ${build.repositoryOwner}/${build.repositoryName}.")

                        dataService.updateStudentTask(getStudentTaskBy(build).with(
                                anyBuilds = true,
                                buildSucceed = false
                        ))
                    }
                    BuildStatus.IN_PROGRESS -> {
                        logger.info("Travis pull request in progress build web hook received " +
                                "for ${build.repositoryOwner}/${build.repositoryName}.")

                        // ignore
                    }
                    else -> {
                        logger.info("Custom travis pull request web hook received " +
                                "for ${build.repositoryOwner}/${build.repositoryName}.")

                        // do nothing
                    }
                }
            else -> {
                logger.info("Custom travis web hook received from request $request.")

                //do nothing
            }
        }

    }

    private fun getStudentTaskBy(hook: TravisPullRequestBuild): StudentTask {
        val user = dataService.getUserByGithubId(hook.repositoryOwner)
                ?: throw Exception("User with the required nickname ${hook.repositoryOwner} wasn't found.")

        val githubCredentials = user.credentials.githubToken
                ?: throw Exception("User ${user.nickname} doesn't have github credentials " +
                        "to get pull request information.")

        val course = dataService.getCourse(hook.repositoryName, user)
                ?: throw Exception("Course with name ${hook.repositoryName} wasn't found " +
                        "for user ${user.nickname}.")

        val pullRequest = gitService.with(githubCredentials)
                .getPullRequest(course.name, hook.number)

        val student = course.students
                .find { it.nickname == pullRequest.authorId }
                ?: throw Exception("Student ${pullRequest.authorId} wasn't found " +
                        "in course ${hook.repositoryOwner}/${hook.repositoryName}.")

        return student.studentTasks
                .find { it.task.name == hook.branch }
                ?: throw Exception("Student task ${hook.branch} wasn't found for student ${student.nickname} " +
                        "in course ${hook.repositoryOwner}/${hook.repositoryName}.")
    }
}