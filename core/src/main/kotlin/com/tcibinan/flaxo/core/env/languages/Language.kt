package com.tcibinan.flaxo.core.env.languages

import com.tcibinan.flaxo.core.env.NamedEntity
import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.frameworks.TestingFramework

abstract class AbstractLanguage(
        private val name: String,
        private val suitableTestLanguages: Set<Language>,
        private val suitableTestingFrameworks: Set<TestingFramework>
) : Language {
    override fun name() = name

    override fun canBeTestedBy(testingLanguage: Language): Boolean =
            testingLanguage == this || testingLanguage in suitableTestLanguages

    override fun worksWith(testingFramework: TestingFramework): Boolean =
            testingFramework in suitableTestingFrameworks
}

interface Language : NamedEntity {
    fun main(buildTool: BuildTool)
    fun test(buildTool: BuildTool)
    fun canBeTestedBy(testingLanguage: Language): Boolean
    fun worksWith(testingFramework: TestingFramework): Boolean
}