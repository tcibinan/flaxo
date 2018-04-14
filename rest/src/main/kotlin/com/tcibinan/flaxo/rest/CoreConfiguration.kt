package com.tcibinan.flaxo.rest

import com.tcibinan.flaxo.core.NamedEntity
import com.tcibinan.flaxo.core.build.BuildTool
import com.tcibinan.flaxo.core.framework.JUnitTestingFramework
import com.tcibinan.flaxo.core.framework.SpekTestingFramework
import com.tcibinan.flaxo.core.framework.TestingFramework
import com.tcibinan.flaxo.core.language.JavaLang
import com.tcibinan.flaxo.core.language.KotlinLang
import com.tcibinan.flaxo.core.language.Language
import com.tcibinan.flaxo.gradle.GradleBuildTool
import com.tcibinan.flaxo.rest.service.environment.RepositoryEnvironmentService
import com.tcibinan.flaxo.rest.service.environment.SimpleRepositoryEnvironmentService
import com.tcibinan.flaxo.travis.env.SimpleTravisEnvironmentSupplier
import com.tcibinan.flaxo.travis.env.TravisEnvironmentSupplier
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

