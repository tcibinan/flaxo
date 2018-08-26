package org.flaxo.rest

import org.flaxo.core.language.Language
import org.flaxo.rest.manager.github.GithubManager
import org.flaxo.rest.manager.moss.MossManager
import org.flaxo.rest.manager.moss.SimpleMossManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Moss configuration.
 */
@Configuration
class MossConfiguration {

    @Bean
    fun mossManager(@Value("\${MOSS_USER_ID}") userId: String,
                    githubManager: GithubManager,
                    supportedLanguages: Map<String, Language>
    ): MossManager =
            SimpleMossManager(userId, githubManager, supportedLanguages)
}