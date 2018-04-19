package org.flaxo.fretrofit

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor for mocking responses by path and method.
 */
class MockInterceptor(private val answers: Map<Pair<String, String>, RetrofitMockResponseDsl>)
    : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestedPath = chain.request().url().encodedPath()
        val requestedMethod = chain.request().method()

        return answers
                .filter { (key, _) ->
                    val (method, pathRegexp) = key

                    method.equals(requestedMethod, true)
                            && requestedPath.matches(Regex(pathRegexp))
                }
                .values
                .firstOrNull()
                ?.response(chain.request())
                ?: throw RuntimeException("Path is not mocked: $requestedMethod:$requestedPath")
    }

}