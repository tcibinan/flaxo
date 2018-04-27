package org.flaxo.rest

import org.flaxo.model.DataService
import org.flaxo.rest.service.travis.TravisService
import org.flaxo.rest.service.travis.TravisSimpleService
import org.flaxo.travis.TravisClient
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
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build()
                    .create(TravisClient::class.java)

    @Bean
    fun travisService(travisClient: TravisClient,
                      dataService: DataService
    ): TravisService =
            TravisSimpleService(travisClient, dataService)
}