package org.flaxo.cmd

import java.io.InputStream
import java.nio.file.Path

/**
 * Command line handy wrapper.
 */
class CmdExecutor private constructor(private val directory: Path?) {

    companion object {
        fun within(directory: Path? = null) = CmdExecutor(directory)
        fun execute(command: String, vararg args: String) = within().execute(command, *args)
    }

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