package org.flaxo.rest

import org.flaxo.core.lang.Language
import org.flaxo.model.DataManager
import org.flaxo.rest.manager.github.GithubManager
import org.flaxo.rest.manager.moss.MossManager
import org.flaxo.rest.manager.moss.MossSubmissionsExtractor
import org.flaxo.rest.manager.moss.SimpleMossManager
import org.flaxo.rest.manager.moss.SimpleMossSubmissionsExtractor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Moss configuration.
 */
@Configuration
class MossConfiguration {

    @Bean
    fun mossSubmissionsExtractor(githubManager: GithubManager, languages: List<Language>): MossSubmissionsExtractor =
            SimpleMossSubmissionsExtractor(githubManager, languages)

    @Bean
    fun mossManager(@Value("\${MOSS_USER_ID}") userId: String,
                    dataManager: DataManager,
                    mossSubmissionsExtractor: MossSubmissionsExtractor
    ): MossManager = SimpleMossManager(userId, dataManager, mossSubmissionsExtractor)
}
