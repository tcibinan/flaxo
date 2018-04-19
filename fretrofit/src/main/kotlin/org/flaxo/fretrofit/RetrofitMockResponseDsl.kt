package org.flaxo.fretrofit

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

/**
 * Holder class for retrofit response mock dsl.
 */
class RetrofitMockResponseDsl {

    private var code = 200
    private var message = "OK"
    private var body: Any? = null
    private val headers: MutableMap<String, String> =
            mutableMapOf("content-type" to "application/json")

    /**
     * Adds a [body] to response.
     */
    fun body(body: Any) {
        this.body = body
    }

    /**
     * Adds a [message] to response.
     */
    fun message(message: String) {
        this.message = message
    }

    /**
     * Adds a [code] to response.
     */
    fun code(code: Int) {
        this.code = code
    }

    /**
     * Adds a [header] to response.
     */
    fun header(header: Pair<String, String>) =
            header(header.first, header.second)

    /**
     * Adds a header with [name] and [value] to response.
     */
    fun header(name: String, value: String) {
        this.headers[name] = value
    }

    /**
     * Generate response based on configured settings and given request.
     */
    fun response(request: Request): Response =
            Response.Builder()
                    .code(code)
                    .message(message)
                    .request(request)
                    .protocol(Protocol.HTTP_2)
                    .also { builder ->
                        body?.let { ObjectMapper().writeValueAsString(it) }
                                .let { it ?: "{}" }
                                .also {
                                    builder.body(ResponseBody.create(MediaType.parse("application/json"), it))
                                }
                    }
                    .also {
                        headers.forEach { key, value -> it.addHeader(key, value) }
                    }
                    .build()

}