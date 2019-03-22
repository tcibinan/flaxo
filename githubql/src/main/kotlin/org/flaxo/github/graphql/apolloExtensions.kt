package org.flaxo.github.graphql

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

/**
 * Returns a deferred that is based on the results of the apollo call.
 */
internal fun <D> ApolloCall<D>.asDeferred(): Deferred<D> {
    val deferred: CompletableDeferred<D> = CompletableDeferred()
    enqueue(object : ApolloCall.Callback<D>() {
        override fun onFailure(e: ApolloException) {
            deferred.cancel(GithubQLException("Apollo exception occurred while performing graphql request", e))
        }

        override fun onResponse(response: Response<D>) {
            if (response.hasErrors()) {
                deferred.cancel(GithubQLException("GraphQL response has errors: " + response.errors().toString()))
            } else {
                val data = response.data()
                if (data != null) {
                    deferred.complete(data)
                } else {
                    deferred.cancel(GithubQLException("No data in graphql response"))
                }
            }
        }

    })
    return deferred
}
