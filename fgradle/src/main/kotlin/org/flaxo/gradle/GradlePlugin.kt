package org.flaxo.gradle

import org.flaxo.core.build.Plugin

data class GradlePlugin(val id: String,
                        val version: String? = null,
                        val dependencies: Set<GradleDependency> = emptySet(),
                        val pluginManagement: GradlePluginManagement? = null
) : Plugin {

    override val name = id
}