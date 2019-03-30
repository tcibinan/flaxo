package org.flaxo.common

/**
 * Flaxo basic exception.
 */
open class FlaxoException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)


/**
 * Basic not found exception.
 */
open class NotFoundException(message: String? = null, cause: Throwable? = null) : FlaxoException(message, cause)
