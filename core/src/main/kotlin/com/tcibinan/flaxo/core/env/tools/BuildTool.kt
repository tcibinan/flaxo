package com.tcibinan.flaxo.core.env.tools

import com.tcibinan.flaxo.core.env.Environment
import com.tcibinan.flaxo.core.env.NamedEntity
import com.tcibinan.flaxo.core.env.frameworks.TestingFramework
import com.tcibinan.flaxo.core.env.languages.Language

interface BuildTool : NamedEntity {

    fun withLanguage(language: Language): BuildTool
    fun withTestingsLanguage(language: Language): BuildTool
    fun withTestingFramework(testingFramework: TestingFramework): BuildTool

    fun addDependency(dependency: Dependency): BuildTool =
            throw UnsupportedOperationException(
                    "${this::class} aka ${name()} doesn't have support for dependencies")

    fun addPlugin(plugin: BuildToolPlugin): BuildTool =
            throw UnsupportedOperationException(
                    "${this::class} aka ${name()} doesn't have support for plugins")

    fun buildEnvironment(): Environment
}