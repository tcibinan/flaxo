package org.flaxo.github.graphql

import arrow.core.Either
import arrow.core.Try
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import org.flaxo.git.AddReviewRequest
import org.flaxo.git.PullRequest
import org.flaxo.git.PullRequestReview

/**
 * Basic Github GraphQL implementation.
 *
 * It uses apollo library to perform graphql calls and parse its results.
 */
class SimpleGithubQL(private val client: ApolloClient) : GithubQL {

    companion object {
        const val MAX_FIRST = 100
        const val MAX_PAGE = 10
    }

    override suspend fun reviews(repository: String, owner: String, pullRequestNumber: Int, lastReviews: Int)
            : Either<Throwable, List<PullRequestReview>> = wrapToEither {
        val query = ReviewsQuery.builder().apply {
            repository(repository)
            owner(owner)
            pullRequestNumber(pullRequestNumber)
            lastReviews(lastReviews)
        }.build()

        query(query)
                .repository
                ?.pullRequest
                ?.reviews
                ?.nodes
                ?.map { GraphQLPullRequestReview.from(it) }
                .orEmpty()
                .filterNotNull()
    }

    override suspend fun pullRequests(repository: String, owner: String)
            : Either<Throwable, List<PullRequest>> = wrapToEither {
        var hasNextPage = true
        var currentPage = 0
        var afterPullRequestCursor: String? = null
        val pullRequests: MutableList<PullRequest> = mutableListOf()
        while (hasNextPage && currentPage < MAX_PAGE) {
            val query = PullRequestsQuery.builder().apply {
                repository(repository)
                owner(owner)
                firstPullRequests(MAX_FIRST)
                afterPullRequestsCursor(afterPullRequestCursor)
                lastCommits(1)
            }.build()

            val pagePullRequests: List<PullRequest> =
                    query(query)
                            .repository
                            ?.pullRequests
                            ?.also {
                                hasNextPage = it.pageInfo.hasNextPage
                                afterPullRequestCursor = it.pageInfo.endCursor
                            }
                            ?.nodes
                            ?.map { GraphQLPullRequest.from(it) }
                            .orEmpty()
                            .filterNotNull()

            pullRequests.addAll(pagePullRequests)
            currentPage += 1
        }
        pullRequests
    }

    override suspend fun addReview(repository: String,
                                   owner: String,
                                   addReviewRequest: AddReviewRequest
    ): Either<Throwable, PullRequestReview> = wrapToEither {
        val mutation = AddReviewMutation.builder().apply {
            input(addReviewRequest.toInput())
        }.build()

        mutate(mutation)
                .addPullRequestReview
                ?.pullRequestReview
                ?.let { GraphQLPullRequestReview.from(it) }
                ?: throw GithubQLException("Add review call didn't return a created review")
    }

    private inline fun <A> wrapToEither(block: () -> A): Either<Throwable, A> = Try { block() }.toEither()

    private suspend fun <D: Operation.Data, V: Operation.Variables, M: Mutation<D, D, V>> mutate(mutation: M): D =
        client.mutate(mutation)
                .asDeferred()
                .await()

    private suspend fun <D : Operation.Data, V : Operation.Variables, Q : Query<D, D, V>> query(query: Q): D =
            client.query(query)
                    .asDeferred()
                    .await()

}
