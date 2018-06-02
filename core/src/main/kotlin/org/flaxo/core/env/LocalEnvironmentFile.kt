package org.flaxo.core.env

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Local environment file.
 */
class LocalEnvironmentFile(location: String) : EnvironmentFile {

    override val path: Path = Paths.get(location)

    override val content: String
        get() = file.useLines { it.joinToString("\n") }

    override val file: File = path.toFile()

}