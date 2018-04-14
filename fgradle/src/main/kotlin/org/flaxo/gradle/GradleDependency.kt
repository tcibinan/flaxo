package org.flaxo.gradle

import org.flaxo.core.build.Dependency

data class GradleDependency(
        val group: String,
        val id: String,
        val version: String,
        val repositories: Set<GradleRepository> = emptySet(),
        val type: GradleDependencyType = GradleDependencyType.COMPILE
) : Dependency {

    override val name = "$group:$id:$version"

    override fun toString() = "$type \"$name\""
}