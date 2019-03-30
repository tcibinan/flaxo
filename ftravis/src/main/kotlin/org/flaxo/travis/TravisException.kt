package org.flaxo.travis

import org.flaxo.common.FlaxoException
import org.flaxo.common.Language

/**
 * Base travis exception.
 */
open class TravisException(message: String) : FlaxoException(message)

/**
 * Travis environment supplier unsupported language exception.
 */
class UnsupportedLanguageException(language: Language)
    : TravisException("Unsupported language ${language.alias} to use with travis")
