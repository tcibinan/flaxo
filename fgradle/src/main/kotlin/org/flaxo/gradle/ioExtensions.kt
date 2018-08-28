package org.flaxo.gradle

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

internal fun Path.fillWith(content: String) = Files.newOutputStream(this).bufferedWriter().write(content)

/**
 * Read all bytes from the original path string.
 */
internal fun String.bytes(): ByteArray = Files.readAllBytes(Paths.get(this))

/**
 * Read and concatenate all lines from the original path string.
 */
internal fun String.lines(): String = Files.readAllLines(Paths.get(this)).joinToString("\n")