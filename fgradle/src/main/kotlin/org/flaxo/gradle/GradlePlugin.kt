package org.flaxo.gradle

import org.flaxo.common.Named

internal data class GradlePlugin(val id: String,
                                 val version: String? = null,
                                 val dependencies: Set<GradleDependency> = emptySet(),
                                 val pluginManagement: GradlePluginManagement? = null
) : Named {

    override val name = id
}
