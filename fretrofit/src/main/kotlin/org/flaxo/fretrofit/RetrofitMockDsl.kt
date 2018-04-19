package org.flaxo.fretrofit

import okhttp3.Interceptor

/**
 * Holder class for retrofit mock dsl.
 */
class RetrofitMockDsl {

    private val answers = mutableMapOf<Pair<String, String>, RetrofitMockResponseDsl>()

    /**
     * @return Http client interceptor with mocked urls.
     */
    fun interceptor(): Interceptor =
            MockInterceptor(answers)

    /**
     * Mocks GET request by [path] regexp.
     */
    fun get(path: String, block: RetrofitMockResponseDsl.() -> Unit = { }) {
        answers["get" to path] = RetrofitMockResponseDsl().also(block)
    }

    /**
     * Mocks POST request by [path] regexp.
     */
    fun post(path: String, block: RetrofitMockResponseDsl.() -> Unit = { }) {
        answers["post" to path] = RetrofitMockResponseDsl().also(block)
    }

}

