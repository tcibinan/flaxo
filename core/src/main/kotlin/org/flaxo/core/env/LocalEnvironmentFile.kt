package org.flaxo.core.env

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Local environment file.
 *
 * Uses server file system to receive file by its location.
 */
class LocalEnvironmentFile(location: String) : EnvironmentFile {

    private val path: Path = Paths.get(location)

    override val name = path.fileName.toString()

    override fun content() =
            path.toFile().useLines { it.joinToString("\n") }

    override fun with(path: String): EnvironmentFile =
            LocalEnvironmentFile(path)

    override fun file(): File = path.toFile()

}