package org.flaxo.rest.manager.notification

import org.apache.logging.log4j.LogManager
import org.flaxo.git.AddReviewRequest
import org.flaxo.git.PullRequestReviewStatus
import org.flaxo.github.GithubException
import org.flaxo.model.data.Course
import org.flaxo.rest.manager.github.GithubManager
import org.springframework.scheduling.annotation.Async
import java.time.LocalDateTime

open class GitHubNotificationManager(private val githubManager: GithubManager) : NotificationManager {

    private val logger = LogManager.getLogger(GitHubNotificationManager::class.java)

    @Async
    override fun notify(course: Course, notifications: List<SolutionNotification>) {
        val user = course.user
        val githubId = user.githubId
                ?: throw GithubException("User ${user.name} doesn't have associated github id")
        val githubToken = user.credentials.githubToken
                ?: throw GithubException("User ${user.name} doesn't have associated github token")
        val repository = githubManager.with(githubToken).getRepository(githubId, course.name)
        val pullRequests = repository.getPullRequests()
        notifications.forEach { notification ->
            val pullRequestNumber = notification.solution.commits
                    .maxBy { it.date ?: LocalDateTime.MIN }
                    ?.pullRequestNumber
            val pullRequest = pullRequests.find { it.number == pullRequestNumber }
            if (pullRequest == null) {
                logger.warn("Pull request #${pullRequestNumber} wasn't found. It will be skipped")
                return
            }
            repository.addPullRequestReview(AddReviewRequest(
                    pullRequestId = pullRequest.id,
                    body = notification.message,
                    state = PullRequestReviewStatus.COMMENTED
            ))
        }
    }
}
