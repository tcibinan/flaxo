package org.flaxo.rest

import org.flaxo.core.NamedEntity
import org.flaxo.core.build.BuildTool
import org.flaxo.core.framework.JUnitTestingFramework
import org.flaxo.core.framework.SpekTestingFramework
import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.language.JavaLang
import org.flaxo.core.language.KotlinLang
import org.flaxo.core.language.Language
import org.flaxo.gradle.GradleBuildTool
import org.flaxo.rest.service.environment.RepositoryEnvironmentService
import org.flaxo.rest.service.environment.SimpleRepositoryEnvironmentService
import org.flaxo.travis.env.SimpleTravisEnvironmentSupplier
import org.flaxo.travis.env.TravisEnvironmentSupplier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoreConfiguration {

    @Bean
    fun supportedLanguages() = namedMap(
            JavaLang,
            KotlinLang
    )

    @Bean
    fun supportedTestingFrameworks() = namedMap(
            JUnitTestingFramework,
            SpekTestingFramework
    )

    @Bean
    fun travisEnvironmentSupplier(@Value("\${TRAVIS_WEB_HOOK_URL}") travisWebHookUrl: String
    ): TravisEnvironmentSupplier =
            SimpleTravisEnvironmentSupplier(travisWebHookUrl = travisWebHookUrl)

    @Bean
    fun defaultBuildTools(travisEnvironmentSupplier: TravisEnvironmentSupplier): Map<Language, BuildTool> =
            mapOf<Language, BuildTool>(
                    JavaLang to GradleBuildTool(travisEnvironmentSupplier),
                    KotlinLang to GradleBuildTool(travisEnvironmentSupplier)
            )


    @Bean
    fun repositoryEnvironmentService(supportedLanguages: Map<String, Language>,
                                     supportedTestingFrameworks: Map<String, TestingFramework>,
                                     defaultBuildTools: Map<Language, BuildTool>
    ): RepositoryEnvironmentService =
            SimpleRepositoryEnvironmentService(
                    supportedLanguages,
                    supportedTestingFrameworks,
                    defaultBuildTools
            )

    private fun <TYPE : NamedEntity> namedMap(vararg namedEntity: TYPE): Map<String, TYPE> =
            namedEntity.groupBy { it.name }
                    .map { (name, entities) -> name to entities.first() }
                    .toMap()
}

