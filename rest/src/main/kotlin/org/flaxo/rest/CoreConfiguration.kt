package org.flaxo.rest

import org.flaxo.common.data.ExternalService
import org.flaxo.common.env.EnvironmentSupplier
import org.flaxo.common.framework.BashInputOutputTestingFramework
import org.flaxo.common.framework.JUnitTestingFramework
import org.flaxo.common.framework.SpekTestingFramework
import org.flaxo.common.framework.TestingFramework
import org.flaxo.common.lang.BashLang
import org.flaxo.common.lang.CLang
import org.flaxo.common.lang.JavaLang
import org.flaxo.common.lang.KotlinLang
import org.flaxo.common.lang.CppLang
import org.flaxo.common.lang.HaskellLang
import org.flaxo.common.lang.JavascriptLang
import org.flaxo.common.lang.Language
import org.flaxo.common.lang.PythonLang
import org.flaxo.common.lang.RLang
import org.flaxo.common.lang.RustLang
import org.flaxo.common.lang.ScalaLang
import org.flaxo.cpp.CppEnvironmentSupplier
import org.flaxo.rest.manager.github.GithubValidationManager
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
            CppLang,
            CLang,
            ScalaLang,
            RLang,
            PythonLang,
            HaskellLang,
            JavascriptLang,
            RustLang,
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
    fun defaultEnvironmentSupplier(travisEnvironmentSupplier: TravisEnvironmentSupplier,
                                   @Value("\${TRAVIS_WEB_HOOK_URL}") travisWebHookUrl: String
    ): Map<Language, EnvironmentSupplier> =
            mapOf(
                    JavaLang to GradleBuildTool(travisEnvironmentSupplier),
                    KotlinLang to GradleBuildTool(travisEnvironmentSupplier),
                    CppLang to CppEnvironmentSupplier(CppLang, BashLang, BashInputOutputTestingFramework,
                            travisWebHookUrl)
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
                          travisManager: TravisManager,
                          githubValidationManager: GithubValidationManager
    ): Map<ExternalService, ValidationManager> = mapOf(
            ExternalService.CODACY to codacyManager,
            ExternalService.TRAVIS to travisManager,
            ExternalService.GITHUB to githubValidationManager
    )
}
