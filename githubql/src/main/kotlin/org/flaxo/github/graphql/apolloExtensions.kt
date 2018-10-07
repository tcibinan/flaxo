package org.flaxo.github.graphql

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.Deferred
import java.lang.RuntimeException


internal fun <D> ApolloQueryCall<D>.asDeferred(): Deferred<D> {
    val deferred: CompletableDeferred<D> = CompletableDeferred()
    enqueue(object : ApolloCall.Callback<D>() {
        override fun onFailure(e: ApolloException) {
            deferred.cancel(e)
        }

        override fun onResponse(response: Response<D>) {
            deferred.complete(response.data() ?: throw RuntimeException("No data in graphql response"))
        }

    })
    return deferred
}