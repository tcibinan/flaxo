package com.tcibinan.flaxo.gradle

import com.tcibinan.flaxo.core.build.Plugin

data class GradlePlugin(
        val id: String,
        val version: String? = null,
        val dependencies: Set<GradleDependency> = emptySet()
) : Plugin {

    override fun name() = id
}