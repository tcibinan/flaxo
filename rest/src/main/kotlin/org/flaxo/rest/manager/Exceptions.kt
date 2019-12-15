package org.flaxo.rest.manager

import org.flaxo.common.FlaxoException
import org.flaxo.common.Framework
import org.flaxo.common.Language
import org.flaxo.common.NotFoundException

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
class IncompatibleTestingFrameworkException(testingFramework: Framework,
                                            testingLanguage: Language
) : Exception("$testingLanguage doesn't support $testingFramework as testing framework")

/**
 * Incompatible testing language for core language exception.
 */
class IncompatibleLanguageException(language: Language,
                                    testingLanguage: Language
) : Exception("$language doesn't support $testingLanguage as language for tests")

/**
 * User not found exception.
 */
class UserNotFoundException(user: String)
    : NotFoundException("User $user was not found.")

/**
 * Course not found exception.
 */
class CourseNotFoundException : NotFoundException {

    constructor(user: String, course: String) : super("Course $user/$course was not found.")

    constructor(id: Long) : super("Course #$id was not found.")
}

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

/**
 * Basic access denied exception.
 */
open class AccessDeniedException(message: String? = null, cause: Throwable? = null) : FlaxoException(message, cause)

/**
 * Course access denied exception.
 */
class CourseAccessDeniedException(user: String, id: Long)
    : AccessDeniedException("User $user does not have access to course #$id.")

/**
 * Plagiarism analysis exception.
 */
class PlagiarismAnalysisException(user: String, course: String, task: String)
    : RuntimeException("Error during analysis of task $user/$course/$task")
