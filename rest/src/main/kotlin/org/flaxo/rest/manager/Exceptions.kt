package org.flaxo.rest.manager

import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.language.Language

/**
 * Unsupported by flaxo testing framework exception.
 */
class UnsupportedTestingFrameworkException(testingFrameworkName: String)
    : Exception("$testingFrameworkName is not supported")

/**
 * Unsupported by flaxo language exception.
 */
class UnsupportedLanguageException(languageName: String)
    : Exception("$languageName is not supported")

/**
 * No default build tool found for language exception.
 */
class NoDefaultBuildToolException(language: Language)
    : Exception("No default build tool for $language")

/**
 * Incompatible testing framework for testing language exception.
 */
class IncompatibleTestingFrameworkException(testingFramework: TestingFramework,
                                            testingLanguage: Language
) : Exception("$testingLanguage doesn't support $testingFramework as testing framework")

/**
 * Incompatible testing language for core language exception.
 */
class IncompatibleLanguageException(language: Language,
                                    testingLanguage: Language
) : Exception("$language doesn't support $testingLanguage as language for tests")