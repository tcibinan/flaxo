package org.flaxo.rest

import org.flaxo.model.DataManager
import org.flaxo.moss.MossSubmissionAnalyser
import org.flaxo.moss.SimpleMoss
import org.flaxo.moss.SimpleMossSubmissionsAnalyser
import org.flaxo.rest.manager.github.GithubManager
import org.flaxo.rest.manager.moss.MossManager
import org.flaxo.rest.manager.moss.MossSubmissionExtractor
import org.flaxo.rest.manager.moss.SimpleMossManager
import org.flaxo.rest.manager.moss.SimpleMossSubmissionsExtractor
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Moss configuration.
 */
@Configuration
class MossConfiguration {

    @Bean
    fun mossSubmissionsExtractor(githubManager: GithubManager): MossSubmissionExtractor =
            SimpleMossSubmissionsExtractor(githubManager)

    @Bean
    fun mossSubmissionsAnalyser(@Value("\${MOSS_USER_ID}") userId: String,
                                githubManager: GithubManager
    ): MossSubmissionAnalyser =
            SimpleMossSubmissionsAnalyser(
                    mossSupplier = { language -> SimpleMoss.of(userId, language) },
                    connectionSupplier = { url -> Jsoup.connect(url) }
            )

    @Bean
    fun mossManager(dataManager: DataManager,
                    mossSubmissionsExtractor: MossSubmissionExtractor,
                    mossSubmissionsAnalyser: MossSubmissionAnalyser
    ): MossManager = SimpleMossManager(dataManager, mossSubmissionsExtractor, mossSubmissionsAnalyser)
}
