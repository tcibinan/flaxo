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

    override fun compatibleTestingLanguages(): Set<Language> = suitableTestLanguages + this

    override fun compatibleTestingFrameworks(): Set<TestingFramework> = suitableTestingFrameworks
}

interface Language : NamedEntity {
    fun main(buildTool: BuildTool)
    fun test(buildTool: BuildTool)

    fun compatibleTestingLanguages(): Set<Language>
    fun compatibleTestingFrameworks(): Set<TestingFramework>

    fun canBeTestedBy(testingLanguage: Language): Boolean =
            testingLanguage == this || testingLanguage in compatibleTestingLanguages()

    fun worksWith(testingFramework: TestingFramework): Boolean =
            testingFramework in compatibleTestingFrameworks()
}