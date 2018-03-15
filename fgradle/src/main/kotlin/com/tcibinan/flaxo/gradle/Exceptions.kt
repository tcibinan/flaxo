package com.tcibinan.flaxo.gradle

/**
 * Base gradle build tool exception.
 */
class GradleException(message: String, cause: Throwable? = null)
    : Throwable(message, cause)