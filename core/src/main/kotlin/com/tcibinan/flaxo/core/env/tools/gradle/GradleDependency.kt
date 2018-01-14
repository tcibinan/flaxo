package com.tcibinan.flaxo.core.env.tools.gradle

import com.tcibinan.flaxo.core.env.tools.Dependency

data class GradleDependency(
        val group: String,
        val id: String,
        val version: String,
        val repositories: Set<GradleRepository> = emptySet(),
        val type: GradleDependencyType = GradleDependencyType.COMPILE
) : Dependency {
    override fun name() = "$group:$id:$version"
    override fun toString() = "$type ${name()}"
}