package org.flaxo.core.env

import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Remote environment file.
 *
 * Loads the given [inputStream] to the local tmp directory by the given [path].
 * **Notice:** Local file and the tmp directory will be deleted only after
 * the jvm will stop. So the proper way to use [RemoteEnvironmentFile] is the
 * one where you call [close] after all calculations have been done.
 */
class RemoteEnvironmentFile(path: String,
                            private val inputStream: InputStream
) : EnvironmentFile {

    override val path: Path = Paths.get(path)

    override val content: String
        get() = file
                .readLines()
                .joinToString("\n")

    override fun inFolder(folder: Path): EnvironmentFile =
            folder.resolve(fileName).toString().let {
                RemoteEnvironmentFile(it, inputStream)
            }

    override val file: File
        get() = path.toFile()
                .takeUnless { it.exists() }
                ?.also {
                    it.parentFile.mkdirs()
                    Files.copy(inputStream, it.toPath())
                }
                ?: path.toFile()

    override fun close() {
        path.toFile().delete()
    }
}