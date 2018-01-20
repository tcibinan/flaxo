package com.tcibinan.flaxo.core.env.tools

import com.tcibinan.flaxo.core.env.Environment
import com.tcibinan.flaxo.core.env.NamedEntity

interface BuildTool : NamedEntity {
    fun addDependency(dependency: Dependency): BuildTool =
            throw UnsupportedOperationException(
                    "${this::class} aka ${name()} doesn't have support for dependencies")

    fun addPlugin(plugin: BuildToolPlugin): BuildTool =
            throw UnsupportedOperationException(
                    "${this::class} aka ${name()} doesn't have support for plugins")

    fun buildEnvironment(): Environment
}