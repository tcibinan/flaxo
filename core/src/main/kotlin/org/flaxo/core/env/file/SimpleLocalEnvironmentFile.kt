package org.flaxo.core.env.file

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class SimpleLocalEnvironmentFile(override val path: Path) : LocalFile {

    constructor(path: String) : this(Paths.get(path))

    override val localPath: Path = path

    override val content: String by lazy(LazyThreadSafetyMode.NONE) {
        Files.readAllLines(localPath).joinToString("\n")
    }

}