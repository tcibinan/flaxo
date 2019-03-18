package org.flaxo.rest.manager

import org.flaxo.common.FlaxoException
import org.flaxo.common.framework.TestingFramework
import org.flaxo.common.lang.Language

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

/**
 * Entity not found exception.
 */
open class NotFoundException(message: String? = null, cause: Throwable? = null) : FlaxoException(message, cause)

/**
 * User not found exception.
 */
class UserNotFoundException(user: String)
    : NotFoundException("User $user was not found.")

/**
 * User not found exception.
 */
class CourseNotFoundException(user: String, course: String)
    : NotFoundException("Course $user/$course was not found.")

/**
 * Task not found exception.
 */
class TaskNotFoundException(user: String, course: String, task: String)
    : NotFoundException("Task $user/$course/$task was not found.")

/**
 * Plagiarism report not found exception.
 */
class PlagiarismReportNotFoundException(user: String, course: String, task: String)
    : NotFoundException("Plagiarism report for task $user/$course/$task was found.")
