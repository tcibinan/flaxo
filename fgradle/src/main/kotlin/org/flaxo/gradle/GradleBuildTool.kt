package org.flaxo.gradle

import org.flaxo.common.Framework
import org.flaxo.common.Language
import org.flaxo.common.data.Named
import org.flaxo.common.env.Environment
import org.flaxo.common.env.EnvironmentSupplier
import org.flaxo.common.env.file.EnvironmentFile

data class GradleBuildTool internal constructor(
        private val travis: EnvironmentSupplier,
        private val dependencies: Set<GradleDependency>,
        private val plugins: Set<GradlePlugin>,
        private val repositories: Set<GradleRepository>
) : Named, EnvironmentSupplier {

    constructor(travis: EnvironmentSupplier) : this(
            travis = travis,
            dependencies = emptySet(),
            plugins = emptySet(),
            repositories = setOf(mavenCentral(), jcenter())
    )

    override val name = "gradle"

    override fun with(language: Language?,
                      testingLanguage: Language?,
                      testingFramework: Framework?
    ): EnvironmentSupplier =
            withLanguage(language)
                    .withTestingLanguage(testingLanguage)
                    .withTestingFramework(testingFramework)
                    .copy(travis = travis.with(language, testingLanguage, testingFramework))

    override fun environment(): Environment =
            gradleWrappers() +
                    gradleBuild() +
                    gradleSettings() +
                    travis.environment()

    private fun gradleSettings(): EnvironmentFile = GradleSettingsFile(plugins)

    private fun gradleBuild(): EnvironmentFile = GradleBuildFile(plugins, repositories, dependencies)

    private fun withLanguage(language: Language?): GradleBuildTool =
            when (language) {
                null -> this

                Language.Java ->
                    addPlugin(javaPlugin())

                Language.Kotlin ->
                    addPlugin(kotlinGradleJvmPlugin())
                            .addDependency(kotlinJreDependency())

                else -> throw UnsupportedLanguageException(language)
            }

    private fun withTestingLanguage(testingLanguage: Language?): GradleBuildTool =
            when (testingLanguage) {
                null -> this

                Language.Java ->
                    addPlugin(junitPlatformPlugin())

                Language.Kotlin ->
                    addPlugin(kotlinGradleJvmPlugin())
                            .addPlugin(junitPlatformPlugin())
                            .addDependency(kotlinJreDependency())
                            .addDependency(kotlinTestDependency())

                else -> throw UnsupportedLanguageException(testingLanguage)
            }

    private fun withTestingFramework(testingFramework: Framework?): GradleBuildTool =
            when (testingFramework) {
                null -> this

                Framework.Spek ->
                    addPlugin(junitPlatformPlugin())
                            .addDependency(spekApiDependency())
                            .addDependency(spekDataDrivenDependency())
                            .addDependency(spekSubjectDependency())
                            .addDependency(spekJunitRunnerDependency())

                Framework.JUnit ->
                    addPlugin(junitPlatformPlugin())
                            .addDependency(jupiterApiDependency())
                            .addDependency(jupiterEngineDependency())

                else -> throw UnsupportedFrameworkException(testingFramework)
            }

    internal fun addDependency(dependency: GradleDependency): GradleBuildTool = copy(
            dependencies = dependencies + dependency,
            repositories = repositories + dependency.repositories
    )

    internal fun addPlugin(plugin: GradlePlugin): GradleBuildTool = copy(plugins = plugins + plugin)

}
