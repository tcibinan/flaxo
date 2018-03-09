package com.tcibinan.flaxo.core.build

import com.tcibinan.flaxo.core.NamedEntity
import com.tcibinan.flaxo.core.env.EnvironmentSupplier
import com.tcibinan.flaxo.core.framework.TestingFramework
import com.tcibinan.flaxo.core.language.Language

/**
 * Build tool interface.
 */
interface BuildTool : NamedEntity, EnvironmentSupplier {

    /**
     * @return BuildTool with the set [language] as language.
     */
    fun withLanguage(language: Language): BuildTool

    /**
     * @return BuildTool with the set [language] as testing language.
     */
    fun withTestingsLanguage(language: Language): BuildTool

    /**
     * @return BuildTool with the set [framework] as testing framework.
     */
    fun withTestingFramework(framework: TestingFramework): BuildTool

    /**
     * @return the build tool with the dependency.
     * @throws UnsupportedOperationException if the current build tool
     * doesn't have support for the given dependency.
     */
    fun addDependency(dependency: Dependency): BuildTool =
            throw UnsupportedOperationException(
                    "Build tool ${name()}:${this::class.simpleName} doesn't have support for dependencies")

    /**
     * @return the build tool with the build tool plugin.
     * @throws UnsupportedOperationException if the current build tool
     * doesn't have support for the given build tool plugin.
     */
    fun addPlugin(plugin: Plugin): BuildTool =
            throw UnsupportedOperationException(
                    "Build tool ${name()}:${this::class.simpleName} doesn't have support for plugins")

}