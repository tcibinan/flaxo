package com.tcibinan.flaxo.core.env.tools.gradle

import com.tcibinan.flaxo.core.env.tools.BuildToolPlugin

data class GradlePlugin(
        val id: String,
        val version: String? = null,
        val dependencies: Set<GradleDependency> = emptySet()
) : BuildToolPlugin {

    override fun name() = id
}