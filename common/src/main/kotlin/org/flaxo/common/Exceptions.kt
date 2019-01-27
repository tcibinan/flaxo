package org.flaxo.common

/**
 * Flaxo basic exception.
 */
open class FlaxoException(message: String? = null, cause: Throwable? = null)
    : RuntimeException(message, cause)
