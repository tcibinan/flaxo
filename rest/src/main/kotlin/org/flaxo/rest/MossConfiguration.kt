package org.flaxo.rest

import org.flaxo.core.language.Language
import org.flaxo.rest.service.git.GitService
import org.flaxo.rest.service.moss.MossService
import org.flaxo.rest.service.moss.SimpleMossService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Moss services configuration.
 */
@Configuration
class MossConfiguration {

    @Bean
    fun mossService(@Value("\${MOSS_USER_ID}") userId: String,
                    gitService: GitService,
                    supportedLanguages: Map<String, Language>
    ): MossService =
            SimpleMossService(userId, gitService, supportedLanguages)
}