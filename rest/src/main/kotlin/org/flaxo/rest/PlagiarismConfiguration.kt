package org.flaxo.rest

import okhttp3.OkHttpClient
import org.flaxo.model.DataManager
import org.flaxo.rest.manager.gitplag.GitplagClient
import org.flaxo.rest.manager.gitplag.GitplagManager
import org.flaxo.rest.manager.moss.MossManager
import org.flaxo.rest.manager.moss.SimpleMossManager
import org.flaxo.rest.manager.plagiarism.BasicPlagiarismManager
import org.flaxo.rest.manager.plagiarism.PlagiarismAnalyser
import org.flaxo.rest.manager.plagiarism.PlagiarismManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

@Configuration
class PlagiarismConfiguration {

    @Bean
    fun mossManager(dataManager: DataManager,
                    plagiarismAnalyser: PlagiarismAnalyser
    ): MossManager = SimpleMossManager(dataManager, plagiarismAnalyser)

    @Bean
    fun plagiarismManager(dataManager: DataManager, mossManager: MossManager): PlagiarismManager =
            BasicPlagiarismManager(dataManager, mossManager)

    @Bean
    fun gitplagManager(gitplagClient: GitplagClient) = GitplagManager(gitplagClient)

    /**
     * [GitplagClient] bean
     */
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
