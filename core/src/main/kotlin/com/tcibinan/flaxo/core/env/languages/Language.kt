package com.tcibinan.flaxo.core.env.languages

import com.tcibinan.flaxo.core.env.NamedEntity
import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.frameworks.TestingFramework

abstract class Language(
        val name: String,
        val suitableTestLanguages: Set<Language>,
        val suitableTestingFrameworks: Set<TestingFramework>
) : NamedEntity {
    override fun name() = name
    abstract fun main(buildTool: BuildTool)
    abstract fun test(buildTool: BuildTool)
}