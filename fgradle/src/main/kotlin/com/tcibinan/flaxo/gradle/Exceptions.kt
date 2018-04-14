package com.tcibinan.flaxo.gradle

import com.tcibinan.flaxo.core.framework.TestingFramework
import com.tcibinan.flaxo.core.language.Language

/**
 * Base gradle build tool exception.
 */
open class GradleException(message: String, cause: Throwable? = null)
    : Throwable(message, cause)

class UnsupportedLanguageException(language: Language)
    : GradleException("Unsupported language ${language.name} for gradle build tool")

class UnsupportedFrameworkException(framework: TestingFramework)
    : GradleException("Unsupported framework ${framework.name} for gradle build tool")