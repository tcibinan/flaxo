package org.flaxo.core.language

import org.flaxo.core.NamedEntity
import org.flaxo.core.framework.TestingFramework

/**
 * Flaxo language interface.
 */
interface Language : NamedEntity {

    /**
     * Extensions of the files written on the language.
     */
    val extensions: Set<String>

    /**
     * Languages that can be used for writing tests for the language.
     */
    val compatibleTestingLanguages: Set<Language>

    /**
     * Frameworks that can be used for testing with the language.
     */
    val compatibleTestingFrameworks: Set<TestingFramework>

    /**
     * Checks if the given [testingLanguage] can be used as testing language
     * for the current language.
     */
    fun canBeTestedBy(testingLanguage: Language): Boolean =
            testingLanguage == this || testingLanguage in compatibleTestingLanguages

    /**
     * Checks if the given [testingFramework] can be used as testing framework
     * by the current language.
     */
    fun worksWith(testingFramework: TestingFramework): Boolean =
            testingFramework in compatibleTestingFrameworks
}