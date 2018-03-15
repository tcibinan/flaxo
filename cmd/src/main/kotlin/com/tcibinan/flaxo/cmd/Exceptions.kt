package com.tcibinan.flaxo.cmd

/**
 * Base command line executor exception.
 */
class CommandLineException(message: String, cause: Throwable? = null)
    : Throwable(message, cause)