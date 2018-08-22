package org.flaxo.cmd

import java.io.File
import java.io.InputStream

/**
 * Command line handy wrapper.
 */
class CmdExecutor private constructor(private val dir: File?) {

    companion object {
        fun within(dir: File? = null) = CmdExecutor(dir)
        fun execute(command: String, vararg args: String) =
                within().execute(command, *args)
    }

    fun execute(command: String, vararg args: String): List<String> = perform(dir, command, *args)

    private fun perform(dir: File?, command: String, vararg args: String): List<String> =
            ProcessBuilder(command, *args)
                    .apply {
                        redirectErrorStream()
                        directory(dir)
                    }
                    .start()
                    .waitForCompletion()

    private fun Process.waitForCompletion(): List<String> =
            if (waitFor() == 0) inputStream.toList()
            else throw CommandLineException(inputStream.toList().joinToString("\n", "\n\n"))

    private fun InputStream.toList() =
            bufferedReader().useLines { it.toList() }

}