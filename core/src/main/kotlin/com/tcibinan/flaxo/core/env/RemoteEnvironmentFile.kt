package com.tcibinan.flaxo.core.env

import io.vavr.kotlin.Try
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
 * one where you call [close] after all calculations have been done. It will
 * delete [file] from the file system if there is one.
 */
class RemoteEnvironmentFile(private val path: String,
                            private val inputStream: InputStream
) : EnvironmentFile {

    private var file: File? = null

    override val name = path

    override fun content(): String =
            (file ?: run { file() })
                    .readLines()
                    .joinToString("\n")


    override fun with(path: String): EnvironmentFile =
            RemoteEnvironmentFile(path, inputStream)

    override fun file(): File = file ?: run {
        val rootDirectory: Path = Files.createTempDirectory("moss-analysis")
                .apply { toFile().deleteOnExit() }

        return Try {
            val fileName = name.split("/").last()

            Paths.get(name.replace(fileName, ""))
                    .let { rootDirectory.resolve(it) }
                    .let { Files.createDirectories(it) }
                    .resolve(fileName)
                    .also { Files.copy(inputStream, it) }
                    .toFile()
                    .also {
                        this.file = it
                    }
        }.onFailure { e ->
            rootDirectory.toFile().deleteRecursively()
            throw RemoteFileRetrievingException(path, e)
        }.get()
    }

    override fun close() {
        file?.delete()
    }
}

class RemoteFileRetrievingException(path: String, cause: Throwable)
    : RuntimeException(path, cause)