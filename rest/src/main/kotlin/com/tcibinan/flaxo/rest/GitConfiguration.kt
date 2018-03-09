package com.tcibinan.flaxo.rest

import com.tcibinan.flaxo.rest.service.git.GitService
import com.tcibinan.flaxo.rest.service.git.GithubService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GitConfiguration {

    @Bean
    fun gitService(@Value("\${GITHUB_WEB_HOOK_URL}") githubWebHookUrl: String
    ): GitService = GithubService(githubWebHookUrl)
}