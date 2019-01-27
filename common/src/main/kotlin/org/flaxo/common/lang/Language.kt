package org.flaxo.common.lang

import org.flaxo.common.NamedEntity
import org.flaxo.common.framework.TestingFramework

/**
 * Programming language.
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
    infix fun canBeTestedBy(testingLanguage: Language): Boolean = testingLanguage in compatibleTestingLanguages

    /**
     * Checks if the given [testingFramework] can be used as testing framework
     * by the current language.
     */
    infix fun worksWith(testingFramework: TestingFramework): Boolean = testingFramework in compatibleTestingFrameworks
}
