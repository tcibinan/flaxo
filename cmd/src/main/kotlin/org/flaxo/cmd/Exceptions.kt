package org.flaxo.cmd

/**
 * Base command line executor exception.
 */
class CommandLineException(commandOutput: String, cause: Throwable? = null)
    : Throwable(commandOutput, cause)