package org.flaxo.rest

import org.flaxo.rest.service.git.GitService
import org.flaxo.rest.service.git.GithubService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Git services configuration.
 */
@Configuration
class GitConfiguration {

    @Bean
    fun gitService(@Value("\${GITHUB_WEB_HOOK_URL}") githubWebHookUrl: String
    ): GitService = GithubService(githubWebHookUrl)
}