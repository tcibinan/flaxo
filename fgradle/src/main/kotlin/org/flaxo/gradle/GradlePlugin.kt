package org.flaxo.gradle

import org.flaxo.core.NamedEntity

internal data class GradlePlugin(val id: String,
                                 val version: String? = null,
                                 val dependencies: Set<GradleDependency> = emptySet(),
                                 val pluginManagement: GradlePluginManagement? = null
) : NamedEntity {

    override val name = id
}