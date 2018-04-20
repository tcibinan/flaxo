package org.flaxo.rest

import org.flaxo.codacy.CodacyClient
import org.flaxo.rest.service.codacy.CodacyService
import org.flaxo.rest.service.codacy.SimpleCodacyService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

/**
 * Codacy services configuration.
 */
@Configuration
class CodacyConfiguration {

    companion object {
        private const val baseUrl = "https://api.codacy.com/2.0/"
    }

    @Bean
    fun codacyClient(): CodacyClient =
            Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build()
                    .create(CodacyClient::class.java)

    @Bean
    fun codacyService(codacyClient: CodacyClient): CodacyService =
            SimpleCodacyService(codacyClient)

}