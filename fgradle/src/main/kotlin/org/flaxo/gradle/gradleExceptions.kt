package org.flaxo.gradle

import org.flaxo.common.FlaxoException
import org.flaxo.common.framework.TestingFramework
import org.flaxo.common.lang.Language

/**
 * Base gradle build tool exception.
 */
open class GradleException(message: String, cause: Throwable? = null)
    : FlaxoException(message, cause)

/**
 * Unsupported language exception.
 */
internal class UnsupportedLanguageException(language: Language)
    : GradleException("Unsupported language ${language.name} for gradle build tool")

/**
 * Unsupported framework exception.
 */
internal class UnsupportedFrameworkException(framework: TestingFramework)
    : GradleException("Unsupported framework ${framework.name} for gradle build tool")
