package org.flaxo.common.cmd

import java.io.InputStream
import java.nio.file.Path

/**
 * Command line handy wrapper.
 */
class CmdExecutor private constructor(private val directory: Path?) {

    companion object {

        /**
         * Creates a cmd executory that executes command under the specified [directory].
         */
        fun within(directory: Path? = null) = CmdExecutor(directory)

        /**
         * Executes [command] with the specified [args] in the current process environment and under its execution
         * directory.
         */
        fun execute(command: String, vararg args: String) = within().execute(command, *args)
    }

    /**
     * Executes [command] with the specified [args] in the current process environment.
     */
    fun execute(command: String, vararg args: String): List<String> = perform(directory, command, *args)

    private fun perform(directory: Path?, command: String, vararg args: String): List<String> =
            ProcessBuilder(command, *args)
                    .also { it.directory(directory?.toFile()) }
                    .start()
                    .waitForCompletion()

    private fun Process.waitForCompletion(): List<String> =
            if (waitFor() == 0) inputStream.toList()
            else throw CommandLineException("Cmd execution failed with the following log: " +
                    (inputStream.toList() + errorStream.toList()).joinToString("\n", "\n\n"))

    private fun InputStream.toList() =
            bufferedReader().useLines { it.toList() }

}