package org.flaxo.core

/**
 * Flaxo basic exception.
 */
open class FlaxoException(message: String? = null, cause: Throwable? = null)
    : RuntimeException(message, cause)