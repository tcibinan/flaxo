package org.flaxo.frontend.client

/**
 * Http call exception.
 */
class FlaxoHttpException(message: String? = null,
                         cause: Throwable? = null,
                         val userMessage: String? = null
) : RuntimeException(message = message, cause = cause)
