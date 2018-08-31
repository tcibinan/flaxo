package org.flaxo.gradle

import org.flaxo.core.FlaxoException
import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.lang.Language

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
