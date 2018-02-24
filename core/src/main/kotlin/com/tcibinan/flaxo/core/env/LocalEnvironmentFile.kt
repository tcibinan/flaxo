package com.tcibinan.flaxo.core.env

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class LocalEnvironmentFile(location: String) : EnvironmentFile {

    private val path: Path = Paths.get(location)

    override fun name() = path.fileName.toString()

    override fun content() =
            path.toFile().useLines { it.joinToString("\n") }

    override fun with(path: String): EnvironmentFile =
            LocalEnvironmentFile(path)

    fun file(): File = path.toFile()

}