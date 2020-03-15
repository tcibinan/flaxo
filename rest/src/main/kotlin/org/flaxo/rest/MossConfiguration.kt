package org.flaxo.rest

import org.flaxo.moss.MossSubmissionAnalyser
import org.flaxo.moss.SimpleMoss
import org.flaxo.moss.SimpleMossSubmissionsAnalyser
import org.flaxo.rest.manager.github.GithubManager
import org.flaxo.rest.manager.moss.MossSubmissionExtractor
import org.flaxo.rest.manager.moss.SimpleMossSubmissionsExtractor
import org.flaxo.rest.manager.moss.MossPlagiarismAnalyser
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Moss configuration.
 */
@Configuration
@ConditionalOnProperty(name = ["flaxo.plagiarism.analyser"], havingValue = "moss")
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
    fun mossPlagiarismAnalyser(mossSubmissionExtractor: MossSubmissionExtractor,
                               mossSubmissionAnalyser: MossSubmissionAnalyser
    ): MossPlagiarismAnalyser = MossPlagiarismAnalyser(mossSubmissionExtractor, mossSubmissionAnalyser)
}
