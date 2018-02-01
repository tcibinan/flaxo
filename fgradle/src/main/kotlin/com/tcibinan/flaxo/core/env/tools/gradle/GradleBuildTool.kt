package com.tcibinan.flaxo.core.env.tools.gradle

import com.tcibinan.flaxo.core.UnsupportedDependencyException
import com.tcibinan.flaxo.core.UnsupportedPluginException
import com.tcibinan.flaxo.core.env.Environment
import com.tcibinan.flaxo.core.env.File
import com.tcibinan.flaxo.core.env.SimpleFile
import com.tcibinan.flaxo.core.env.frameworks.JUnitTestingFramework
import com.tcibinan.flaxo.core.env.frameworks.SpekTestingFramework
import com.tcibinan.flaxo.core.env.frameworks.TestingFramework
import com.tcibinan.flaxo.core.env.languages.JavaLang
import com.tcibinan.flaxo.core.env.languages.KotlinLang
import com.tcibinan.flaxo.core.env.languages.Language
import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.tools.BuildToolPlugin
import com.tcibinan.flaxo.core.env.tools.Dependency
import java.util.*

class GradleBuildTool : BuildTool {

    private val dependencies = mutableSetOf<GradleDependency>()
    private val plugins = mutableSetOf<GradlePlugin>()
    private val pluginsDependencies = mutableSetOf<GradleDependency>()
    private val pluginsRepositories = mutableSetOf(mavenCentral(), jcenter())
    private val repositories = mutableSetOf(mavenCentral(), jcenter())

    override fun name() = "gradle"

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
        when (dependency) {
            is GradleDependency -> {
                dependencies.add(dependency)
                repositories.addAll(dependency.repositories)
            }
            else -> throw UnsupportedDependencyException(dependency, this)
        }
        return this
    }

    override fun addPlugin(plugin: BuildToolPlugin): BuildTool {
        when (plugin) {
            is GradlePlugin -> {
                plugins.add(plugin)
                pluginsDependencies.addAll(plugin.dependencies)
                pluginsRepositories.addAll(plugin.dependencies.flatMap { it.repositories }.toSet())
            }
            else -> throw UnsupportedPluginException(plugin, this)
        }
        return this
    }

    override fun buildEnvironment(): Environment {
        val joiner = StringJoiner("\n")

        joiner.addPlugins(pluginsRepositories, pluginsDependencies, plugins)
                .addRepositories(repositories)
                .addDependencies(dependencies)

        return object : Environment {
            override fun getFiles(): Set<File> =
                    setOf(
                            SimpleFile("build.gradle", joiner.toString())
                    )

        }
    }

    private fun StringJoiner.addDependencies(dependencies: MutableCollection<GradleDependency>): StringJoiner {
        if (dependencies.count() > 0) {
            add("dependencies {")
            dependencies.forEach { add("""    $it""") }
            add("}")
        }
        return this
    }

    private fun StringJoiner.addIndependentPlugins(plugins: MutableCollection<GradlePlugin>): StringJoiner {
        if (plugins.count() > 0) {
            add("plugins {")
            plugins.forEach { add("""    id "${it.id}"""") }
            add("}")
        }
        return this
    }

    private fun StringJoiner.addRepositories(repositories: MutableCollection<GradleRepository>): StringJoiner {
        add("repositories {")
        repositories.forEach { add("""    ${it.address}""") }
        add("}")
        return this
    }

    private fun StringJoiner.addBuildScript(
            pluginsRepositories: MutableCollection<GradleRepository>,
            pluginsDependencies: MutableCollection<GradleDependency>,
            plugins: MutableCollection<GradlePlugin>
    ) {
        add("buildscript {")
        addRepositories(pluginsRepositories)
        addDependencies(pluginsDependencies)
        add("}")
        addPluginApplyings(plugins)
    }

    private fun StringJoiner.addPluginApplyings(plugins: MutableCollection<GradlePlugin>) {
        plugins.forEach { add("""apply plugin: "${it.id}"""") }
    }

    private fun StringJoiner.addPlugins(
            pluginsRepositories: MutableCollection<GradleRepository>,
            pluginsDependencies: MutableCollection<GradleDependency>,
            plugins: MutableCollection<GradlePlugin>
    ): StringJoiner {
        if (pluginsDependencies.count() > 0) {
            addBuildScript(pluginsRepositories, pluginsDependencies, plugins)
        } else {
            addIndependentPlugins(plugins)
        }
        return this
    }

    inner class UnsupportedLanguage(language: Language)
        : Throwable("Unsupported language ${language.name()} for ${name()} build tool")

    inner class UnsupportedFramework(framework: TestingFramework)
        : Throwable("Unsupported framework ${framework.name()} for ${name()} build tool")

}