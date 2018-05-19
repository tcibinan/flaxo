package org.flaxo.rest

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.flaxo.model.DataService
import org.flaxo.rest.service.git.GitService
import org.flaxo.rest.service.travis.TravisService
import org.flaxo.rest.service.travis.SimpleTravisService
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
        private const val baseUrl = "https://api.travis-ci.org/"
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
    fun travisService(travisClient: TravisClient,
                      dataService: DataService,
                      gitService: GitService
    ): TravisService =
            SimpleTravisService(travisClient, dataService, gitService)
}