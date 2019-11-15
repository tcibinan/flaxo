package org.flaxo.gradle

import java.nio.file.Files
import java.nio.file.Paths

/**
 * Read all bytes from the original path string.
 */
internal fun String.bytes(): ByteArray = Files.readAllBytes(Paths.get(this))
