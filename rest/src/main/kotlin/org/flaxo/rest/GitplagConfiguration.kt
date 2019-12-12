package org.flaxo.rest

import org.flaxo.moss.GitplagClient
import org.flaxo.rest.manager.plagiarism.GitplagPlagiarismAnalyser
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

/**
 * Codacy configuration.
 */
@Configuration
class GitplagConfiguration {

    @Bean
    fun gitplagClient(
            @Value("\${flaxo.gitplag.url}") gitplagUrl: String
    ): GitplagClient =
            Retrofit.Builder()
                    .baseUrl(gitplagUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build()
                    .create(GitplagClient::class.java)

    @Bean
    fun gitplagPlagiarismAnalyser(
            gitplagClient: GitplagClient,
            @Value("\${flaxo.gitplag.ui.url}") gitplagUiUrl: String
    ): GitplagPlagiarismAnalyser =
            GitplagPlagiarismAnalyser(gitplagClient, gitplagUiUrl)
}