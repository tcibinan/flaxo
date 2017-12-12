package com.tcibinan.flaxo.rest

import com.tcibinan.flaxo.rest.services.GitService
import com.tcibinan.flaxo.rest.services.GithubService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GitConfiguration {

    @Bean
    fun gitService(): GitService = GithubService()
}