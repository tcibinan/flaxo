package org.flaxo.frontend.client

class FlaxoHttpCallException(message: String?, cause: Throwable? = null)
    : RuntimeException(message = message, cause = cause)
