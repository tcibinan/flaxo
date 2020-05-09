package org.flaxo.rest

import okhttp3.OkHttpClient
import org.flaxo.rest.manager.gitplag.GitplagClient
import org.flaxo.rest.manager.gitplag.GitplagManager
import org.flaxo.rest.manager.gitplag.GitplagPlagiarismAnalyser
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Gitplag configuration.
 */
@Configuration
class GitplagConfiguration {

    /**
     * [GitplagPlagiarismAnalyser] bean
     */
    @Bean
    @ConditionalOnProperty(name = ["flaxo.plagiarism.analyser"], havingValue = "gitplag")
    fun gitplagPlagiarismAnalyser(
            gitplagClient: GitplagClient,
            @Value("\${flaxo.gitplag.ui.url}") gitplagUiUrl: String
    ): GitplagPlagiarismAnalyser =
            GitplagPlagiarismAnalyser(gitplagClient, gitplagUiUrl)

    @Bean
    fun gitplagManager(gitplagClient: GitplagClient) = GitplagManager(gitplagClient)

    @Bean
    fun gitplagClient(
            @Value("\${flaxo.gitplag.url}") gitplagUrl: String,
            @Value("\${flaxo.gitplag.timeout.seconds}") timeoutInSeconds: Long
    ): GitplagClient {
        val okHttpClient = OkHttpClient.Builder()
                .readTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .build()
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(gitplagUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(GitplagClient::class.java)
    }
}
