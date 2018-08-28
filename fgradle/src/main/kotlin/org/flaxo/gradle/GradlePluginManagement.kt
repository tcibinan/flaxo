package org.flaxo.gradle

internal data class GradlePluginManagement(val id: String,
                                           val dependency: GradleDependency,
                                           val repositories: Set<GradleRepository>
)