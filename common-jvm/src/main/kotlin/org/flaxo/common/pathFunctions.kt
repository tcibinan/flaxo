package org.flaxo.common

import java.nio.file.Files
import java.nio.file.Path

/**
 * Deletes all directory child files and directories.
 */
fun deleteDirectoryRecursively(directory: Path) =
        Files.walk(directory)
                .sorted(Comparator.reverseOrder())
                .forEach { Files.delete(it) }
