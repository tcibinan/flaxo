package org.flaxo.rest

import org.flaxo.common.ExternalService
import org.flaxo.core.NamedEntity
import org.flaxo.core.env.EnvironmentSupplier
import org.flaxo.core.framework.BashInputOutputTestingFramework
import org.flaxo.core.framework.JUnitTestingFramework
import org.flaxo.core.framework.SpekTestingFramework
import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.lang.BashLang
import org.flaxo.core.lang.JavaLang
import org.flaxo.core.lang.KotlinLang
import org.flaxo.core.lang.`C++Lang`
import org.flaxo.core.lang.Language
import org.flaxo.cpp.CppEnvironmentSupplier
import org.flaxo.gradle.GradleBuildTool
import org.flaxo.rest.manager.ValidationManager
import org.flaxo.rest.manager.codacy.CodacyManager
import org.flaxo.rest.manager.statistics.CsvStatisticsManager
import org.flaxo.rest.manager.statistics.JsonStatisticsManager
import org.flaxo.rest.manager.statistics.StatisticsManager
import org.flaxo.rest.manager.environment.EnvironmentManager
import org.flaxo.rest.manager.environment.SimpleEnvironmentManager
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
    fun languages(): List<Language> = listOf(
            JavaLang,
            KotlinLang,
            `C++Lang`,
            BashLang
    )

    @Bean
    fun testingFrameworks(): List<TestingFramework> = listOf(
            JUnitTestingFramework,
            SpekTestingFramework,
            BashInputOutputTestingFramework
    )

    @Bean
    fun travisEnvironmentSupplier(@Value("\${TRAVIS_WEB_HOOK_URL}") travisWebHookUrl: String
    ): TravisEnvironmentSupplier =
            SimpleTravisEnvironmentSupplier(travisWebHookUrl = travisWebHookUrl)

    @Bean
    fun defaultEnvironmentSupplier(travisEnvironmentSupplier: TravisEnvironmentSupplier
    ): Map<Language, EnvironmentSupplier> =
            mapOf(
                    JavaLang to GradleBuildTool(travisEnvironmentSupplier),
                    KotlinLang to GradleBuildTool(travisEnvironmentSupplier),
                    `C++Lang` to CppEnvironmentSupplier(`C++Lang`, BashLang, BashInputOutputTestingFramework)
            )

    @Bean
    fun repositoryEnvironmentService(languages: List<Language>,
                                     testingFrameworks: List<TestingFramework>,
                                     defaultEnvironmentSuppliers: Map<Language, EnvironmentSupplier>
    ): EnvironmentManager =
            SimpleEnvironmentManager(
                    languages,
                    testingFrameworks,
                    defaultEnvironmentSuppliers
            )

    @Bean
    fun statisticsConverters(): Map<String, StatisticsManager> = mapOf(
            "json" to JsonStatisticsManager(),
            "csv" to CsvStatisticsManager(),
            "tsv" to TsvStatisticsManager()
    )

    @Bean
    fun courseValidations(codacyManager: CodacyManager,
                          travisManager: TravisManager
    ): Map<ExternalService, ValidationManager> = mapOf(
            ExternalService.CODACY to codacyManager,
            ExternalService.TRAVIS to travisManager
    )

    private fun <TYPE : NamedEntity> namedMap(vararg namedEntity: TYPE): Map<String, TYPE> =
            namedEntity.groupBy { it.name }
                    .map { (name, entities) -> name to entities.first() }
                    .toMap()
}

