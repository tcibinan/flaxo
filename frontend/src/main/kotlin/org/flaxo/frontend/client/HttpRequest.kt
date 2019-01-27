package org.flaxo.frontend.client

import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.Deferred
import org.flaxo.common.data.Payload
import org.flaxo.frontend.Credentials
import org.flaxo.frontend.wrapper.btoa
import org.flaxo.frontend.wrapper.encodeURIComponent
import org.w3c.xhr.XMLHttpRequest

class HttpRequest<T> {
    lateinit var url: String
    lateinit var httpMethod: String
    lateinit var apiMethod: String
    var creds: Credentials? = null
    var errorMessage: String? = null
    lateinit var onSuccess: (String) -> T
    var onFailure: ((String?) -> Unit)? = null
    var params: Map<String, Any?> = emptyMap()
    var body: Any? = null

    suspend fun execute(): T =
            with(performedRequest()) {
                if (status.toInt() == 200) {
                    onSuccess(responseText)
                } else {
                    with(errorPayload(this)) {
                        onFailure?.invoke(this)
                        throw FlaxoHttpException(errorMessage, userMessage = this)
                    }
                }
            }

    private suspend fun performedRequest(): XMLHttpRequest =
            try {
                httpCall(
                        httpMethod = httpMethod,
                        apiMethod = apiMethod,
                        credentials = creds,
                        parameters = params,
                        body = body
                ).await()
            } catch (e: Throwable) {
                onFailure?.invoke(null)
                throw FlaxoHttpException(errorMessage, e)
            }

    private fun httpCall(httpMethod: String,
                         apiMethod: String,
                         parameters: Map<String, Any?> = emptyMap(),
                         body: Any? = null,
                         credentials: Credentials? = null
    ): Deferred<XMLHttpRequest> =
            with(XMLHttpRequest()) {
                if (parameters.isEmpty()) {
                    open(httpMethod, "$url$apiMethod", async = true)
                } else {
                    val parametersString = parameters.filterValues { it != null }
                            .map { (key, value) -> "$key=${encodeURIComponent(value.toString())}" }
                            .joinToString("&")
                    open(httpMethod, "$url$apiMethod?$parametersString", async = true)
                }
                if (credentials != null) {
                    setRequestHeader("Authorization", authorizationToken(credentials))
                }
                if (body != null) {
                    setRequestHeader("Content-Type", "application/json")
                    send(JSON.stringify(body))
                } else {
                    send()
                }
                CompletableDeferred<XMLHttpRequest>().also { deferred ->
                    onreadystatechange = { if (readyState == XMLHttpRequest.DONE) deferred.complete(this) }
                }
            }

    private fun authorizationToken(credentials: Credentials) =
            "Basic " + btoa(credentials.username + ":" + credentials.password)

    private fun errorPayload(request: XMLHttpRequest): String? =
            try {
                JSON.parse<Payload<String>>(request.responseText).payload
            } catch (e: Throwable) {
                onFailure?.invoke(null)
                throw FlaxoHttpException(errorMessage + "\n" + request.responseText)
            }
}
