package com.tcibinan.flaxo.core.env

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import java.io.File
import java.io.InputStream
import java.nio.file.Files


class RemoteEnvironmentFile(private val path: String,
                            private val inputStream: InputStream
) : EnvironmentFile {


    override fun name() = path

    override fun content(): String =
            inputStream.reader()
                    .useLines { it.joinToString("\n") }

    fun file(): File {
        val fs = Jimfs.newFileSystem(Configuration.unix())
        val inMemoryFile = fs.getPath(path)

        inputStream.use { Files.copy(it, inMemoryFile) }

        return inMemoryFile.toFile()
    }
}

