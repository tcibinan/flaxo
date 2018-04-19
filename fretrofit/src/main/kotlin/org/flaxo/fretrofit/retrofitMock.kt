package org.flaxo.fretrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

/**
 * Returns a mocked retrofit based on a provided dsl [block].
 */
fun retrofitMock(block: RetrofitMockDsl.() -> Unit
): Retrofit {
    val interceptor = RetrofitMockDsl()
            .also(block)
            .interceptor()
    val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    return Retrofit.Builder()
            .baseUrl("http://mock.retrofit.url/")
            .addConverterFactory(JacksonConverterFactory.create())
            .client(client)
            .build()
}