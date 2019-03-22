package org.flaxo.gradle

import org.flaxo.common.data.Named
import org.flaxo.common.env.Environment
import org.flaxo.common.env.EnvironmentSupplier
import org.flaxo.common.env.file.EnvironmentFile
import org.flaxo.common.framework.JUnitTestingFramework
import org.flaxo.common.framework.SpekTestingFramework
import org.flaxo.common.framework.TestingFramework
import org.flaxo.common.lang.JavaLang
import org.flaxo.common.lang.KotlinLang
import org.flaxo.common.lang.Language

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
                      testingFramework: TestingFramework?
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

                JavaLang ->
                    addPlugin(javaPlugin())

                KotlinLang ->
                    addPlugin(kotlinGradleJvmPlugin())
                            .addDependency(kotlinJreDependency())

                else -> throw UnsupportedLanguageException(language)
            }

    private fun withTestingLanguage(testingLanguage: Language?): GradleBuildTool =
            when (testingLanguage) {
                null -> this

                JavaLang ->
                    addPlugin(junitPlatformPlugin())

                KotlinLang ->
                    addPlugin(kotlinGradleJvmPlugin())
                            .addPlugin(junitPlatformPlugin())
                            .addDependency(kotlinJreDependency())
                            .addDependency(kotlinTestDependency())

                else -> throw UnsupportedLanguageException(testingLanguage)
            }

    private fun withTestingFramework(testingFramework: TestingFramework?): GradleBuildTool =
            when (testingFramework) {
                null -> this

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
            }

    internal fun addDependency(dependency: GradleDependency): GradleBuildTool = copy(
            dependencies = dependencies + dependency,
            repositories = repositories + dependency.repositories
    )

    internal fun addPlugin(plugin: GradlePlugin): GradleBuildTool = copy(plugins = plugins + plugin)

}
