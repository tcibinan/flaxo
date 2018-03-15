package com.tcibinan.flaxo.github

/**
 * Base github exception.
 */
class GithubException(message: String, cause: Throwable? = null)
    : Throwable(message, cause)