package org.flaxo.frontend.client

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.serialization.serializer
import org.flaxo.frontend.Container
import org.flaxo.frontend.Credentials
import org.flaxo.frontend.wrapper.btoa
import org.flaxo.frontend.wrapper.encodeURIComponent
import org.w3c.xhr.XMLHttpRequest

/**
 * Http request handler class.
 *
 * It can be predefined to perform calls to a specific [url] and [apiMethod]. The response body is transformed
 * to the required type [T] if the proper [onSuccess] function is provided as well.
 */
class HttpRequest<T> {

    /**
     * API root url.
     */
    lateinit var url: String

    /**
     * Request HTTP method.
     */
    lateinit var httpMethod: String

    /**
     * Request API method.
     */
    lateinit var apiMethod: String

    /**
     * Credentials object.
     */
    var creds: Credentials? = null

    /**
     * Error message if something went wrong.
     */
    var errorMessage: String? = null

    /**
     * Response body transform function.
     */
    lateinit var onSuccess: (String) -> T

    /**
     * Failure side-effect function.
     */
    var onFailure: ((String?) -> Unit)? = null

    /**
     * Request parameters map
     */
    var params: Map<String, Any?> = emptyMap()

    /**
     * Request body object.
     */
    var body: String? = null

    /**
     * Performs the configured http request and returns its body transformed to type [T].
     */
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
                         body: String? = null,
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
                    send(body)
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
                Container.json.parse(String.serializer(), request.responseText)
            } catch (e: Throwable) {
                onFailure?.invoke(null)
                throw FlaxoHttpException(errorMessage + "\n" + request.responseText)
            }
}
