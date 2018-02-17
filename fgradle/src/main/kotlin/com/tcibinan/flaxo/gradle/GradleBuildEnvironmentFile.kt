package com.tcibinan.flaxo.gradle

import com.tcibinan.flaxo.core.env.EnvironmentFile
import java.util.*

class GradleBuildEnvironmentFile private constructor(private val content: String)
    : EnvironmentFile {

    override fun name() = "build.gradle"
    override fun content() = content

    companion object Builder {

        private val joiner = StringJoiner("\n")

        fun addPlugins(pluginsRepositories: Set<GradleRepository>,
                       pluginsDependencies: Set<GradleDependency>,
                       plugins: Set<GradlePlugin>
        ): GradleBuildEnvironmentFile.Builder = this.apply {
            joiner.addPlugins(pluginsRepositories, pluginsDependencies, plugins)
        }

        fun addRepositories(repositories: Set<GradleRepository>)
                : GradleBuildEnvironmentFile.Builder = this.apply {
            joiner.addRepositories(repositories)
        }

        fun addDependencies(dependencies: Set<GradleDependency>)
                : GradleBuildEnvironmentFile.Builder = this.apply {
            joiner.addDependencies(dependencies)
        }

        fun build() = GradleBuildEnvironmentFile(joiner.toString())

        private fun StringJoiner.addDependencies(dependencies: Set<GradleDependency>): StringJoiner {
            if (dependencies.count() > 0) {
                add("dependencies {")
                dependencies.forEach { add("""    $it""") }
                add("}")
            }
            return this
        }

        private fun StringJoiner.addIndependentPlugins(plugins: Set<GradlePlugin>): StringJoiner {
            if (plugins.count() > 0) {
                add("plugins {")
                plugins.forEach { add("""    id "${it.id}"""") }
                add("}")
            }
            return this
        }

        private fun StringJoiner.addRepositories(repositories: Set<GradleRepository>): StringJoiner {
            add("repositories {")
            repositories.forEach { add("""    ${it.address}""") }
            add("}")
            return this
        }

        private fun StringJoiner.addBuildScript(
                pluginsRepositories: Set<GradleRepository>,
                pluginsDependencies: Set<GradleDependency>,
                plugins: Set<GradlePlugin>
        ) {
            add("buildscript {")
            addRepositories(pluginsRepositories)
            addDependencies(pluginsDependencies)
            add("}")
            addPluginsApplying(plugins)
        }

        private fun StringJoiner.addPluginsApplying(plugins: Set<GradlePlugin>) {
            plugins.forEach { add("""apply plugin: "${it.id}"""") }
        }

        private fun StringJoiner.addPlugins(
                pluginsRepositories: Set<GradleRepository>,
                pluginsDependencies: Set<GradleDependency>,
                plugins: Set<GradlePlugin>
        ): StringJoiner {
            if (pluginsDependencies.count() > 0) {
                addBuildScript(pluginsRepositories, pluginsDependencies, plugins)
            } else {
                addIndependentPlugins(plugins)
            }
            return this
        }
    }
}