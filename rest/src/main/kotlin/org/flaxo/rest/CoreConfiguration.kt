package org.flaxo.rest

import org.flaxo.common.Framework
import org.flaxo.common.Language
import org.flaxo.common.data.ExternalService
import org.flaxo.common.env.EnvironmentSupplier
import org.flaxo.cpp.CppEnvironmentSupplier
import org.flaxo.gradle.GradleBuildTool
import org.flaxo.rest.manager.ValidationManager
import org.flaxo.rest.manager.codacy.CodacyManager
import org.flaxo.rest.manager.environment.EnvironmentManager
import org.flaxo.rest.manager.environment.SimpleEnvironmentManager
import org.flaxo.rest.manager.github.GithubValidationManager
import org.flaxo.rest.manager.statistics.CsvStatisticsManager
import org.flaxo.rest.manager.statistics.JsonStatisticsManager
import org.flaxo.rest.manager.statistics.StatisticsManager
import org.flaxo.rest.manager.statistics.TsvStatisticsManager
import org.flaxo.rest.manager.travis.TravisManager
import org.flaxo.travis.env.SimpleTravisEnvironmentSupplier
import org.flaxo.travis.env.TravisEnvironmentSupplier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Flaxo core configurations.
 */
@Configuration
class CoreConfiguration {

    @Bean
    fun travisEnvironmentSupplier(@Value("\${flaxo.travis.hook.url}") travisWebHookUrl: String
    ): TravisEnvironmentSupplier =
            SimpleTravisEnvironmentSupplier(travisWebHookUrl = travisWebHookUrl)

    @Bean
    fun defaultEnvironmentSupplier(travisEnvironmentSupplier: TravisEnvironmentSupplier,
                                   @Value("\${flaxo.travis.hook.url}") travisWebHookUrl: String
    ): Map<Language, EnvironmentSupplier> =
            mapOf(
                    Language.Java to GradleBuildTool(travisEnvironmentSupplier),
                    Language.Kotlin to GradleBuildTool(travisEnvironmentSupplier),
                    Language.Cpp to CppEnvironmentSupplier(Language.Cpp, Language.Bash, Framework.BashIO,
                            travisWebHookUrl)
            )

    @Bean
    fun repositoryEnvironmentService(defaultEnvironmentSuppliers: Map<Language, EnvironmentSupplier>
    ): EnvironmentManager = SimpleEnvironmentManager(defaultEnvironmentSuppliers)

    @Bean
    fun statisticsConverters(): Map<String, StatisticsManager> = mapOf(
            "json" to JsonStatisticsManager(),
            "csv" to CsvStatisticsManager(),
            "tsv" to TsvStatisticsManager()
    )

    @Bean
    fun courseValidations(codacyManager: CodacyManager,
                          travisManager: TravisManager,
                          githubValidationManager: GithubValidationManager
    ): Map<ExternalService, ValidationManager> = mapOf(
            ExternalService.CODACY to codacyManager,
            ExternalService.TRAVIS to travisManager,
            ExternalService.GITHUB to githubValidationManager
    )
}
