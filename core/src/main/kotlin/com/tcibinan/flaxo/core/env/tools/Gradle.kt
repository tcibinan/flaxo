package com.tcibinan.flaxo.core.env.tools

import com.tcibinan.flaxo.core.UnsupportedDependencyException
import com.tcibinan.flaxo.core.UnsupportedPluginException
import com.tcibinan.flaxo.core.env.Environment
import com.tcibinan.flaxo.core.env.File
import com.tcibinan.flaxo.core.env.SimpleFile
import java.util.*

class GradleBuildTool : BuildTool {

    private val dependencies = mutableSetOf<GradleDependency>()
    private val plugins = mutableSetOf<GradlePlugin>()
    private val pluginsDependencies = mutableSetOf<GradleDependency>()
    private val pluginsRepositories = mutableSetOf<GradleRepository>()
    private val repositories = mutableSetOf(
            GradleRepository("maven()"),
            GradleRepository("mavenCentral()"),
            GradleRepository("jcenter()")
    )

    override fun name() = "gradle"

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
}

data class GradlePlugin(
        val id: String,
        val version: String? = null,
        val dependencies: Set<GradleDependency> = emptySet()
) : BuildToolPlugin {

    override fun name() = id
}

data class GradleDependency(
        val group: String,
        val name: String,
        val version: String,
        val repositories: Set<GradleRepository> = emptySet(),
        private val test: Boolean = false
) : Dependency {

    override fun name() = "$group:$name:$version"
    fun isTesting() = test
}

data class GradleRepository(
        val address: String
)

private fun StringJoiner.addDependencies(dependencies: MutableCollection<GradleDependency>): StringJoiner {
    if (dependencies.count() > 0) {
        add("dependencies {")
        dependencies.forEach {
            val dependencyString = """${it.group}:${it.name}:${it.version}"""
            add("""    ${if (it.isTesting()) "testCompile" else "compile"} "$dependencyString" """)
        }
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
