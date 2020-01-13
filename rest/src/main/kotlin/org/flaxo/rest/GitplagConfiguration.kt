package org.flaxo.rest

import org.flaxo.rest.manager.gitplag.GitplagClient
import org.flaxo.rest.manager.plagiarism.GitplagPlagiarismAnalyser
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Gitplag configuration.
 */
@Configuration
@ConditionalOnProperty(name = ["flaxo.plagiarism.analyser"], havingValue = "gitplag")
class GitplagConfiguration {

    /**
     * [GitplagPlagiarismAnalyser] bean
     */
    @Bean
    fun gitplagPlagiarismAnalyser(
            gitplagClient: GitplagClient,
            @Value("\${flaxo.gitplag.ui.url}") gitplagUiUrl: String
    ): GitplagPlagiarismAnalyser =
            GitplagPlagiarismAnalyser(gitplagClient, gitplagUiUrl)
}