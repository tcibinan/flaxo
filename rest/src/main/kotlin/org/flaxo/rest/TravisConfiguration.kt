package org.flaxo.rest

import org.flaxo.rest.service.travis.TravisService
import org.flaxo.rest.service.travis.TravisSimpleService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TravisConfiguration {

    val baseUrl = "https://api.travis-ci.org"

    @Bean
    fun travisService(): TravisService = TravisSimpleService(baseUrl)
}