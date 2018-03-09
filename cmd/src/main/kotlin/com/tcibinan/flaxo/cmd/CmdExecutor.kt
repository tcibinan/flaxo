package com.tcibinan.flaxo.cmd

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

    fun execute(command: String, vararg args: String): List<String> {
        return perform(dir, command, *args)
    }

    private fun perform(dir: File?, command: String, vararg args: String): List<String> {
        return completed(ProcessBuilder(command, *args).directory(dir).start())
    }

    private fun completed(process: Process): List<String> {
        return when (process.waitFor()) {
            0 -> process.inputStream.toList()
            else -> throw Exception(
                    process.errorStream.toList()
                            .joinToString("\n", "\n\n")
            )
        }
    }

    private fun InputStream.toList() =
            bufferedReader().useLines { it.toList() }

}