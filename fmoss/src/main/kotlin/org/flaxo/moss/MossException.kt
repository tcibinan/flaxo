package org.flaxo.moss

/**
 * Base moss analysis exception.
 */
class MossException: Throwable {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}