package org.flaxo.github.graphql

import arrow.core.Either
import arrow.core.Try
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import okhttp3.OkHttpClient
import org.flaxo.git.PullRequest
import org.flaxo.git.PullRequestReview
import org.flaxo.github.graphql.type.CustomType

internal class SimpleGithubQL(githubToken: String,
                              githubV4Endpoint: String = "https://api.github.com/graphql"
) : GithubQL {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
                .authenticator { _, response ->
                    response.request()
                            .newBuilder()
                            .header("Authorization", "bearer $githubToken")
                            .build()
                }
                .build()
    }

    private val apolloClient: ApolloClient by lazy {
        ApolloClient.builder().apply {
            serverUrl(githubV4Endpoint)
            okHttpClient(okHttpClient)
            addCustomTypeAdapter(CustomType.DATETIME, DateTimeCustomTypeAdapter())
        }.build()
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
                ?.map { GraphQLPullRequestReview(it) }
                .orEmpty()

    }

    override suspend fun pullRequests(repository: String, owner: String, lastPullRequests: Int, lastCommits: Int)
            : Either<Throwable, List<PullRequest>> = wrapToEither {
        val query = PullRequestsQuery.builder().apply {
            repository(repository)
            owner(owner)
            lastPullRequests(lastPullRequests)
            lastCommits(lastCommits)
        }.build()

        query(query)
                .repository
                ?.pullRequests
                ?.nodes
                ?.map { GraphQLPullRequest(it) }
                .orEmpty()
    }

    private inline fun <A> wrapToEither(block: () -> A): Either<Throwable, A> = Try { block() }.toEither()

    private suspend fun <D : Operation.Data, V : Operation.Variables, Q : Query<D, D, V>> query(query: Q): D =
            apolloClient
                    .query(query)
                    .asDeferred()
                    .await()

}
