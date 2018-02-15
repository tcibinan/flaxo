package com.tcibinan.flaxo.rest.service.travis

import com.tcibinan.flaxo.cmd.perform
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class TravisSimpleService(private val baseUrl: String) : TravisService {

    override fun retrieveTravisToken(githubUsername: String, githubToken: String): String {
        perform("travis", "login",
                "-u", githubUsername,
                "-g", githubToken)

        return perform("travis", "token")
                .first().split(" ").last()
    }

    override fun travis(travisToken: String): Travis {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(TravisClient::class.java)
                .with(travisToken)
    }

    private fun TravisClient.with(travisToken: String): Travis =
            Travis(this, travisToken)

}
