package com.tcibinan.flaxo.cmd

import java.io.File
import java.io.InputStream

fun perform(dir: File, command: String, vararg args: String): List<String> {
    return completed(ProcessBuilder(command, *args).directory(dir).start())
}

fun perform(command: String, vararg args: String): List<String> {
    return completed(ProcessBuilder(command, *args).start())
}

fun completed(process: Process): List<String> {
    return when (process.waitFor()) {
        0 -> process.inputStream.toList()
        else -> throw RuntimeException(process.errorStream.toList().joinToString())
    }
}

private fun InputStream.toList() =
        bufferedReader().useLines { it.toList() }
