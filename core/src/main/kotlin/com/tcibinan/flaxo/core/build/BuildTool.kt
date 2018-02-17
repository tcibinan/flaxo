package com.tcibinan.flaxo.core.build

import com.tcibinan.flaxo.core.NamedEntity
import com.tcibinan.flaxo.core.env.EnvironmentTool
import com.tcibinan.flaxo.core.framework.TestingFramework
import com.tcibinan.flaxo.core.language.Language

interface BuildTool : NamedEntity, EnvironmentTool {

    fun withLanguage(language: Language): BuildTool
    fun withTestingsLanguage(language: Language): BuildTool
    fun withTestingFramework(framework: TestingFramework): BuildTool

    fun addDependency(dependency: Dependency): BuildTool =
            throw UnsupportedOperationException(
                    "${this::class} aka ${name()} doesn't have support for dependencies")

    fun addPlugin(plugin: BuildToolPlugin): BuildTool =
            throw UnsupportedOperationException(
                    "${this::class} aka ${name()} doesn't have support for plugins")

}