package com.tcibinan.flaxo.core.language

import com.tcibinan.flaxo.core.NamedEntity
import com.tcibinan.flaxo.core.framework.TestingFramework

/**
 * Flaxo language interface.
 */
interface Language : NamedEntity {

    /**
     * Extension of the files written on the language.
     */
    val extension: String

    /**
     * Returns languages that can be used for writing tests for the language.
     *
     * @return set of compatible testing languages.
     */
    fun compatibleTestingLanguages(): Set<Language>

    /**
     * Returns language testing frameworks.
     *
     * @return set if compatible testing frameworks.
     */
    fun compatibleTestingFrameworks(): Set<TestingFramework>

    /**
     * Checks if the given [testingLanguage] can be used as testing language
     * for the current language.
     */
    fun canBeTestedBy(testingLanguage: Language): Boolean =
            testingLanguage == this || testingLanguage in compatibleTestingLanguages()

    /**
     * Checks if the given [testingFramework] can be used as testing framework
     * by the current language.
     */
    fun worksWith(testingFramework: TestingFramework): Boolean =
            testingFramework in compatibleTestingFrameworks()
}