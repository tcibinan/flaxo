package org.flaxo.gradle

import org.flaxo.core.UnsupportedDependencyException
import org.flaxo.core.UnsupportedPluginException
import org.flaxo.core.build.BuildTool
import org.flaxo.core.build.Dependency
import org.flaxo.core.build.Plugin
import org.flaxo.core.env.Environment
import org.flaxo.core.env.EnvironmentFile
import org.flaxo.core.env.EnvironmentSupplier
import org.flaxo.core.framework.JUnitTestingFramework
import org.flaxo.core.framework.SpekTestingFramework
import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.language.JavaLang
import org.flaxo.core.language.KotlinLang
import org.flaxo.core.language.Language

data class GradleBuildTool(private val travis: EnvironmentSupplier,
                           private val dependencies: Set<GradleDependency> = emptySet(),
                           private val plugins: Set<GradlePlugin> = emptySet(),
                           private val repositories: Set<GradleRepository> = setOf(mavenCentral(), jcenter())
) : BuildTool {

    override val name = "gradle"

    override fun withLanguage(language: Language): GradleBuildTool =
            when (language) {
                JavaLang ->
                    addPlugin(javaPlugin())

                KotlinLang ->
                    addPlugin(kotlinGradleJvmPlugin())
                            .addDependency(kotlinJreDependency())

                else -> throw UnsupportedLanguageException(language)
            }.copy(travis = travis.withLanguage(language))

    override fun withTestingLanguage(testingLanguage: Language): GradleBuildTool =
            when (testingLanguage) {
                JavaLang ->
                    addPlugin(junitPlatformPlugin())

                KotlinLang ->
                    addPlugin(kotlinGradleJvmPlugin())
                            .addPlugin(junitPlatformPlugin())
                            .addDependency(kotlinJreDependency())
                            .addDependency(kotlinTestDependency())

                else -> throw UnsupportedLanguageException(testingLanguage)
            }.copy(travis = travis.withTestingLanguage(testingLanguage))

    override fun withTestingFramework(testingFramework: TestingFramework): GradleBuildTool =
            when (testingFramework) {
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

                else -> throw UnsupportedFrameworkException(testingFramework)
            }.copy(travis = travis.withTestingFramework(testingFramework))

    override fun addDependency(dependency: Dependency): GradleBuildTool =
            when (dependency) {
                is GradleDependency -> copy(
                        dependencies = dependencies + dependency,
                        repositories = repositories + dependency.repositories
                )
                else -> throw UnsupportedDependencyException(dependency, this)
            }

    override fun addPlugin(plugin: Plugin): GradleBuildTool =
            when (plugin) {
                is GradlePlugin -> copy(plugins = plugins + plugin)
                else -> throw UnsupportedPluginException(plugin, this)
            }

    override fun getEnvironment(): Environment =
            gradleEnvironment() + travis.getEnvironment()

    private fun gradleEnvironment(): Environment {
        val gradleBuild = produceGradleBuild()
        val gradleSettings = produceGradleSettings()
        return GradleWrappers.with(gradleBuild, gradleSettings)
                .plus(gradleBuild)
                .plus(gradleSettings)
    }

    private fun produceGradleSettings(): EnvironmentFile =
            GradleSettingsFile.with(plugins)

    private fun produceGradleBuild(): EnvironmentFile =
            GradleBuildFile.with(plugins, repositories, dependencies)

}
