package org.flaxo.rest.manager.github

import org.apache.logging.log4j.LogManager
import org.flaxo.git.PullRequestReviewStatus.APPROVED
import org.flaxo.git.PullRequestReviewStatus.CHANGES_REQUESTED
import org.flaxo.model.DataManager
import org.flaxo.model.ModelException
import org.flaxo.model.data.Course
import org.flaxo.rest.manager.ValidationManager

/**
 * GitHub validation manager.
 *
 * Validates solutions based on the pull request reviews.
 */
class GithubValidationManager(private val githubManager: GithubManager,
                              private val dataManager: DataManager
) : ValidationManager {

    private val logger = LogManager.getLogger(GithubValidationManager::class.java)

    override fun activate(course: Course) = Unit

    override fun deactivate(course: Course) = Unit

    override fun refresh(course: Course) {
        val user = course.user

        val githubToken = user.credentials.githubToken
                ?: throw ModelException("Github token is not specified for ${user.name}")

        logger.info("Github reviews refreshing is started for ${user.name}/${course.name} course")

        val repository = githubManager.with(githubToken).getRepository(course.name)

        course.tasks
                .flatMap { it.solutions }
                .forEach { solution ->
                    solution.commits.lastOrNull()
                            ?.pullRequestNumber
                            ?.let { repository.getPullRequestReviews(it) }
                            ?.filter { it.user == user.githubId }
                            ?.filter { it.status in setOf(APPROVED, CHANGES_REQUESTED) }
                            ?.lastOrNull()
                            ?.let { review -> solution.copy(approved = review.status == APPROVED) }
                            ?.let { dataManager.updateSolution(it) }
                }

        logger.info("Github reviews refreshing has finished for ${user.name}/${course.name} course")
    }
}
