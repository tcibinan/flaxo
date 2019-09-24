package org.flaxo.rest

import org.flaxo.moss.GitplagClient
import org.flaxo.rest.manager.plagiarism.GitplagPlagiarismAnalyser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

/**
 * Codacy configuration.
 */
@Configuration
class GitplagConfiguration {

    companion object {
        private const val baseUrl = "http://localhost:8090/"
    }

    @Bean
    fun gitplagClient(): GitplagClient =
            Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build()
                    .create(GitplagClient::class.java)

    @Bean
    fun gitplagPlagiarismAnalyser(gitplagClient: GitplagClient): GitplagPlagiarismAnalyser =
            GitplagPlagiarismAnalyser(gitplagClient)
}