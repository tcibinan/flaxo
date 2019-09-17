package org.flaxo.rest.manager.github

import org.apache.logging.log4j.LogManager
import org.flaxo.git.GitPayload
import org.flaxo.git.PullRequest
import org.flaxo.github.Github
import org.flaxo.github.GithubException
import org.flaxo.github.graphql.GithubQL
import org.flaxo.github.parseGithubEvent
import org.flaxo.model.DataManager
import java.io.Reader
import org.kohsuke.github.GitHub as KohsukeGithub

/**
 * Github manager implementation.
 */
class SimpleGithubManager(private val dataManager: DataManager,
                          private val webHookUrl: String
) : GithubManager {

    private val logger = LogManager.getLogger(SimpleGithubManager::class.java)

    override fun with(credentials: String) =
            Github(
                    githubClientProducer = { KohsukeGithub.connectUsingOAuth(credentials) },
                    rawWebHookUrl = webHookUrl,
                    githubQL = GithubQL.from(credentials)
            )

    override fun parsePayload(reader: Reader, headers: Map<String, List<String>>): GitPayload? {
        val types = headers["x-github-event"].orEmpty()
                .takeIf { it.isNotEmpty() }
                ?: throw GithubException("Github payload type wasn't found in headers.")
        return parseGithubEvent(reader, types.first(), KohsukeGithub.connectAnonymously())
    }

    override fun upsertPullRequest(pullRequest: PullRequest) {
        val user = dataManager.getUserByGithubId(pullRequest.receiverLogin)
                ?: throw GithubException("User with githubId ${pullRequest.receiverLogin} wasn't found in database.")

        val course = dataManager.getCourse(pullRequest.receiverRepositoryName, user)
                ?: throw GithubException("Course ${pullRequest.receiverRepositoryName} wasn't found " +
                        "for user ${user.nickname}.")

        val student = course.students
                .find { it.name == pullRequest.authorLogin }
                ?: dataManager.addStudent(pullRequest.authorLogin, course).also {
                    logger.info("Student ${it.name} was initialised " +
                            "for course ${user.nickname}/${course.name}.")
                }

        student.solutions
                .find { it.task.branch == pullRequest.targetBranch }
                ?.takeIf { it.commits.lastOrNull()?.sha != pullRequest.lastCommitSha }
                ?.also { solution ->
                    logger.info("Add ${pullRequest.lastCommitSha} commit to ${student.name} student solution " +
                            "for course ${user.nickname}/${course.name}.")
                    dataManager.addCommit(solution, pullRequest.number, pullRequest.lastCommitSha)
                }
    }
}
