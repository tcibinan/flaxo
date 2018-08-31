package org.flaxo.travis

import org.flaxo.core.FlaxoException
import org.flaxo.core.lang.Language

/**
 * Base travis exception.
 */
open class TravisException(message: String) : FlaxoException(message)

/**
 * Travis environment supplier unsupported language exception.
 */
class UnsupportedLanguageException(language: Language)
    : TravisException("Unsupported language ${language.name} to use with travis")