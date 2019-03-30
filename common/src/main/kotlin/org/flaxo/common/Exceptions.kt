package org.flaxo.common

/**
 * Flaxo basic exception.
 */
open class FlaxoException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)


/**
 * Basic not found exception.
 */
open class NotFoundException(message: String? = null, cause: Throwable? = null) : FlaxoException(message, cause)

/**
 * Language not found exception.
 */
class LanguageNotFoundException(alias: String) : NotFoundException("Language with alias $alias was not found.")

/**
 * Framework not found exception.
 */
class FrameworkNotFoundException(alias: String) : NotFoundException("Framework with alias $alias was not found.")
