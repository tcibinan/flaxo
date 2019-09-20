package org.flaxo.rest

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.flaxo.model.DataManager
import org.flaxo.rest.manager.github.GithubManager
import org.flaxo.rest.manager.travis.SimpleTravisManager
import org.flaxo.rest.manager.travis.TravisManager
import org.flaxo.rest.manager.travis.TravisTokenSupplier
import org.flaxo.travis.retrofit.TravisClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

/**
 * Travis services configuration.
 */
@Configuration
class TravisConfiguration {

    companion object {
        private const val baseUrl = "https://api.travis-ci.com/"
    }

    @Bean
    fun travisClient(): TravisClient =
            Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(JacksonConverterFactory.create(
                            ObjectMapper().registerModules(KotlinModule(), JavaTimeModule())
                                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    ))
                    .build()
                    .create(TravisClient::class.java)

    @Bean
    fun travisTokenSupplier(): TravisTokenSupplier = TravisTokenSupplier()

    @Bean
    fun travisService(
            travisClient: TravisClient,
            tokenSupplier: TravisTokenSupplier,
            dataManager: DataManager,
            githubManager: GithubManager
    ): TravisManager =
            SimpleTravisManager(travisClient, tokenSupplier, dataManager, githubManager)
}
