package org.flaxo.rest

import org.flaxo.model.DataManager
import org.flaxo.rest.manager.github.GithubManager
import org.flaxo.rest.manager.github.GithubValidationManager
import org.flaxo.rest.manager.github.SimpleGithubManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Github configuration.
 */
@Configuration
class GithubConfiguration {

    @Bean
    fun githubManager(@Value("\${GITHUB_WEB_HOOK_URL}") githubWebHookUrl: String): GithubManager =
            SimpleGithubManager(githubWebHookUrl)

    @Bean
    fun githubValidationManager(githubManager: GithubManager, dataManager: DataManager): GithubValidationManager =
            GithubValidationManager(githubManager, dataManager)
}
