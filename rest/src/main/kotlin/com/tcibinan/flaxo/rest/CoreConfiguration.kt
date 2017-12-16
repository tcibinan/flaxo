package com.tcibinan.flaxo.rest

import com.tcibinan.flaxo.core.env.NamedEntity
import com.tcibinan.flaxo.core.env.frameworks.JUnit4TestingFramework
import com.tcibinan.flaxo.core.env.frameworks.SpekTestingFramework
import com.tcibinan.flaxo.core.env.frameworks.TestingFramework
import com.tcibinan.flaxo.core.env.languages.JavaLang
import com.tcibinan.flaxo.core.env.languages.KotlinLang
import com.tcibinan.flaxo.core.env.languages.Language
import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.tools.GradleBuildTool
import com.tcibinan.flaxo.rest.services.RepositoryEnvironmentService
import com.tcibinan.flaxo.rest.services.SimpleRepositoryEnvironmentService
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
            JUnit4TestingFramework,
            SpekTestingFramework
    )

    @Bean
    fun defaultBuildTools() = mapOf<Language, BuildTool>(
            JavaLang to GradleBuildTool(),
            KotlinLang to GradleBuildTool()
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
}

private fun <TYPE : NamedEntity> namedMap(vararg languages: TYPE): Map<String, TYPE> =
        languages.groupBy { it.name() }
                .map { (name, entities) -> name to entities[0] }
                .toMap()