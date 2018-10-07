package org.flaxo.github.graphql

import arrow.core.Either
import org.flaxo.git.PullRequest
import org.flaxo.git.PullRequestReview

/**
 * Github GraphQL interface.
 */
interface GithubQL {

    companion object {
        fun from(githubToken: String, githubV4Endpoint: String? = null): GithubQL =
                githubV4Endpoint?.let { SimpleGithubQL(githubToken, it) }
                        ?: SimpleGithubQL(githubToken)
    }

    /**
     * Returns a list of pull requests reviews of the [repository] of [owner] user by a [pullRequestNumber]
     * or throwable if something went wrong.
     */
    suspend fun reviews(repository: String, owner: String, pullRequestNumber: Int, lastReviews: Int = 10)
            : Either<Throwable, List<PullRequestReview>>

    /**
     * Returns a list of pull requests of the [repository] of [owner] user or throwable if something went wrong.
     */
    suspend fun pullRequests(repository: String, owner: String, lastPullRequests: Int, lastCommits: Int = 1)
            : Either<Throwable, List<PullRequest>>
}
