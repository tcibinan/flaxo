package com.tcibinan.flaxo.gradle

import com.tcibinan.flaxo.core.UnsupportedDependencyException
import com.tcibinan.flaxo.core.UnsupportedPluginException
import com.tcibinan.flaxo.core.env.Environment
import com.tcibinan.flaxo.core.framework.JUnitTestingFramework
import com.tcibinan.flaxo.core.framework.SpekTestingFramework
import com.tcibinan.flaxo.core.framework.TestingFramework
import com.tcibinan.flaxo.core.language.JavaLang
import com.tcibinan.flaxo.core.language.KotlinLang
import com.tcibinan.flaxo.core.language.Language
import com.tcibinan.flaxo.core.build.BuildTool
import com.tcibinan.flaxo.core.build.BuildToolPlugin
import com.tcibinan.flaxo.core.build.Dependency
import com.tcibinan.flaxo.core.env.EnvironmentFile
import com.tcibinan.flaxo.travis.env.SimpleTravisEnvironmentTool

class GradleBuildTool(private val language: Language,
                      private val testingLanguage: Language,
                      private val framework: TestingFramework,
                      private val dependencies: Set<GradleDependency> = emptySet(),
                      private val plugins: Set<GradlePlugin> = emptySet(),
                      private val pluginsDependencies: Set<GradleDependency> = emptySet(),
                      private val pluginsRepositories: Set<GradleRepository> = setOf(mavenCentral(), jcenter()),
                      private val repositories: Set<GradleRepository> = setOf(mavenCentral(), jcenter())
) : BuildTool {

    override fun name() = "gradle"

    constructor(gradleBuildTool: GradleBuildTool,
                language: Language? = null,
                testingLanguage: Language? = null,
                framework: TestingFramework? = null,
                dependencies: Set<GradleDependency> = emptySet(),
                plugins: Set<GradlePlugin> = emptySet(),
                pluginsDependencies: Set<GradleDependency> = emptySet(),
                pluginsRepositories: Set<GradleRepository> = setOf(mavenCentral(), jcenter()),
                repositories: Set<GradleRepository> = setOf(mavenCentral(), jcenter()))
            : this(
            language ?: gradleBuildTool.language,
            testingLanguage ?: gradleBuildTool.testingLanguage,
            framework ?: gradleBuildTool.framework,
            gradleBuildTool.dependencies + dependencies,
            gradleBuildTool.plugins + plugins,
            gradleBuildTool.pluginsDependencies + pluginsDependencies,
            gradleBuildTool.pluginsRepositories + pluginsRepositories,
            gradleBuildTool.repositories + repositories
    )

    override fun withLanguage(language: Language): BuildTool {
        return when (language) {
            JavaLang -> addPlugin(javaPlugin())

            KotlinLang -> addPlugin(junitPlatformPlugin())
                    .addDependency(kotlinTestDependency())

            else -> throw UnsupportedLanguage(language)
        }
    }

    override fun withTestingsLanguage(language: Language): BuildTool {
        return when (language) {
            JavaLang -> addPlugin(junitPlatformPlugin())

            KotlinLang ->
                addPlugin(kotlinGradlePlugin())
                        .addDependency(kotlinJreDependency())

            else -> throw UnsupportedLanguage(language)
        }
    }

    override fun withTestingFramework(framework: TestingFramework): BuildTool {
        return when (framework) {
            SpekTestingFramework ->
                addPlugin(junitPlatformPlugin())
                        .addDependency(spekApiDependency())
                        .addDependency(spekDataDrivenDependency())
                        .addDependency(spekSubjectDependency())
                        .addDependency(spekJunitRunnerDependency())

            JUnitTestingFramework ->
                addPlugin(junitPlatformPlugin())
                        .addDependency(jupiterApiDependency())
                        .addDependency(jupiterEngineDependency())

            else -> throw UnsupportedFramework(framework)
        }
    }

    override fun addDependency(dependency: Dependency): BuildTool {
        return when (dependency) {
            is GradleDependency -> GradleBuildTool(this,
                    dependencies = dependencies + dependency,
                    repositories = repositories + dependency.repositories
            )
            else -> throw UnsupportedDependencyException(dependency, this)
        }
    }

    override fun addPlugin(plugin: BuildToolPlugin): BuildTool {
        return when (plugin) {
            is GradlePlugin -> GradleBuildTool(this,
                    plugins = plugins + plugin,
                    pluginsDependencies = pluginsDependencies + plugin.dependencies,
                    pluginsRepositories = pluginsRepositories + plugin.dependencies.flatMap { it.repositories }.toSet()
            )
            else -> throw UnsupportedPluginException(plugin, this)
        }
    }

    override fun produceEnvironment(): Environment =
            withLanguage(language)
                    .withTestingsLanguage(testingLanguage)
                    .withTestingFramework(framework)
                    .run { gradleEnvironment() + travisEnvironment() }

    private fun travisEnvironment(): Environment =
            SimpleTravisEnvironmentTool(language, testingLanguage, framework)
                    .produceEnvironment()

    private fun gradleEnvironment(): Environment {
        val gradleBuild = produceGradleBuild()
        return GradleWrappers.with(gradleBuild) + gradleBuild
    }

    private fun produceGradleBuild(): EnvironmentFile =
            GradleBuildEnvironmentFile.builder()
                    .addPlugins(pluginsRepositories, pluginsDependencies, plugins)
                    .addRepositories(repositories)
                    .addDependencies(dependencies)
                    .build()

    inner class UnsupportedLanguage(language: Language)
        : Throwable("Unsupported language ${language.name()} for ${name()} build tool")

    inner class UnsupportedFramework(framework: TestingFramework)
        : Throwable("Unsupported framework ${framework.name()} for ${name()} build tool")

}
