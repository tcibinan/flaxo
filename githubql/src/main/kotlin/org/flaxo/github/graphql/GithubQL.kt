package org.flaxo.github.graphql

import arrow.core.Either
import org.flaxo.git.AddReviewRequest
import org.flaxo.git.PullRequest
import org.flaxo.git.PullRequestReview

/**
 * Github GraphQL interface.
 */
interface GithubQL {

    companion object {

        /**
         * Builds an instance of GithubQL with specified [githubToken].
         */
        fun from(githubToken: String): GithubQL = DefaultGithubQL(githubToken)
    }

    /**
     * Returns a list of pull requests reviews of the [repository] of [owner] user by a [pullRequestNumber]
     * or throwable.
     */
    suspend fun reviews(repository: String, owner: String, pullRequestNumber: Int, lastReviews: Int = 10)
            : Either<Throwable, List<PullRequestReview>>

    /**
     * Returns a list of pull requests of the [repository] of [owner] user or throwable.
     */
    suspend fun pullRequests(repository: String, owner: String): Either<Throwable, List<PullRequest>>

    /**
     * Add a pull request review to a pull request with [pullRequestId] from [repository] of [owner]
     * according to the given [addReviewRequest].
     *
     * @return The created review or throwable.
     */
    suspend fun addReview(repository: String, owner: String, addReviewRequest: AddReviewRequest)
            : Either<Throwable, PullRequestReview>
}
