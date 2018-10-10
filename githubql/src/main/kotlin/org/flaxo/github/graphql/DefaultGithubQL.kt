package org.flaxo.github.graphql

import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient
import org.flaxo.github.graphql.type.CustomType

private fun okHttpClient(githubToken: String): OkHttpClient =
        OkHttpClient.Builder()
                .authenticator { _, response ->
                    response.request()
                            .newBuilder()
                            .header("Authorization", "bearer $githubToken")
                            .build()
                }
                .build()

private fun apolloClient(githubV4Endpoint: String, okHttpClient: OkHttpClient): ApolloClient =
    ApolloClient.builder().apply {
        serverUrl(githubV4Endpoint)
        okHttpClient(okHttpClient)
        addCustomTypeAdapter(CustomType.DATETIME, DateTimeCustomTypeAdapter())
    }.build()

internal class DefaultGithubQL(githubToken: String)
    : GithubQL by SimpleGithubQL(apolloClient("https://api.github.com/graphql", okHttpClient(githubToken)))
