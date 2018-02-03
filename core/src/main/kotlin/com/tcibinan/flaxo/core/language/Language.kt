package com.tcibinan.flaxo.core.language

import com.tcibinan.flaxo.core.NamedEntity
import com.tcibinan.flaxo.core.framework.TestingFramework

interface Language : NamedEntity {
    fun compatibleTestingLanguages(): Set<Language>
    fun compatibleTestingFrameworks(): Set<TestingFramework>

    fun canBeTestedBy(testingLanguage: Language): Boolean =
            testingLanguage == this || testingLanguage in compatibleTestingLanguages()

    fun worksWith(testingFramework: TestingFramework): Boolean =
            testingFramework in compatibleTestingFrameworks()
}