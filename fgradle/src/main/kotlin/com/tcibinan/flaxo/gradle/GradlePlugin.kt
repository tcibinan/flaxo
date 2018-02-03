package com.tcibinan.flaxo.gradle

import com.tcibinan.flaxo.core.build.BuildToolPlugin

data class GradlePlugin(
        val id: String,
        val version: String? = null,
        val dependencies: Set<GradleDependency> = emptySet()
) : BuildToolPlugin {

    override fun name() = id
}