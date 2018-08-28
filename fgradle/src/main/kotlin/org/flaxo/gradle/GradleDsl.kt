package org.flaxo.gradle

import java.util.StringJoiner

internal class GradleDsl(val joiner: StringJoiner = StringJoiner("\n"),
                         val nesting: Int = 0
) {
    fun put(line: String, nesting: Int = this.nesting) {
        val indent = "    ".repeat(nesting)
        joiner.add(indent + line)
    }

    fun buildScript(block: GradleDsl.() -> Unit) =
            expression("buildscript", block)

    fun pluginManagement(block: GradleDsl.() -> Unit) =
            expression("pluginManagement", block)

    fun resolutionStrategy(block: GradleDsl.() -> Unit) =
            expression("resolutionStrategy", block)

    fun eachPlugin(block: GradleDsl.() -> Unit) =
            expression("eachPlugin", block)

    fun dependencies(block: GradleDsl.() -> Unit) =
            expression("dependencies", block)

    fun repositories(block: GradleDsl.() -> Unit) =
            expression("repositories", block)

    fun plugins(block: GradleDsl.() -> Unit) =
            expression("plugins", block)

    fun repository(repository: GradleRepository) =
            put(repository.address)

    fun dependency(dependency: GradleDependency) =
            put(dependency.toString())

    fun applyPlugin(plugin: GradlePlugin) =
            put("apply plugin: \"${plugin.id}\"")

    private fun expression(title: String, block: GradleDsl.() -> Unit) {
        put("$title {")
        GradleDsl(joiner, nesting = nesting + 1).block()
        put("}")
    }
}

internal fun gradle(block: GradleDsl.() -> Unit): String =
        GradleDsl()
                .also { it.block() }
                .joiner
                .toString()