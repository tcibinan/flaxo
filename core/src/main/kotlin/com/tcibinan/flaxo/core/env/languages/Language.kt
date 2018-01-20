package com.tcibinan.flaxo.core.env.languages

import com.tcibinan.flaxo.core.env.NamedEntity
import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.frameworks.TestingFramework

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