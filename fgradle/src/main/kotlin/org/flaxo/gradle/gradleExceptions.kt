package org.flaxo.gradle

import org.flaxo.common.FlaxoException
import org.flaxo.common.Framework
import org.flaxo.common.Language

/**
 * Base gradle build tool exception.
 */
open class GradleException(message: String, cause: Throwable? = null)
    : FlaxoException(message, cause)

/**
 * Unsupported language exception.
 */
internal class UnsupportedLanguageException(language: Language)
    : GradleException("Unsupported language ${language.alias} for gradle build tool")

/**
 * Unsupported framework exception.
 */
internal class UnsupportedFrameworkException(framework: Framework)
    : GradleException("Unsupported framework ${framework.alias} for gradle build tool")
