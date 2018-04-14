package org.flaxo.rest.service

import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.language.Language

/**
 * Unsupported by flaxo testing framework exception.
 */
class UnsupportedTestingFramework(testingFrameworkName: String)
    : Throwable("$testingFrameworkName is not supported")

/**
 * Unsupported by flaxo language exception.
 */
class UnsupportedLanguage(languageName: String)
    : Throwable("$languageName is not supported")

/**
 * No default build tool found for language exception.
 */
class NoDefaultBuildTool(language: Language)
    : Throwable("No default build tool for $language")

/**
 * Incompatible testing framework for testing language exception.
 */
class IncompatibleTestingFramework(testingFramework: TestingFramework, testingLanguage: Language)
    : Throwable("$testingLanguage doesn't support $testingFramework as testing framework")

/**
 * Incompatible testing language for core language exception.
 */
class IncompatibleLanguage(language: Language, testingLanguage: Language)
    : Throwable("$language doesn't support $testingLanguage as language for tests")

/**
 * Absent environment property exception.
 */
class AbsentEnvironmentPropertyException(property: String)
    : Throwable("$property is not set in the current environment.")