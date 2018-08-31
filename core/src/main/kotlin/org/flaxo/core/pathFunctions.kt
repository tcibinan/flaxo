package org.flaxo.core

import java.nio.file.Files
import java.nio.file.Path

fun deleteDirectoryRecursively(directory: Path) =
        Files.walk(directory)
                .sorted(Comparator.reverseOrder())
                .forEach { Files.delete(it) }