package org.flaxo.common.env.file

import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

/**
 * Lazy local environment file.
 *
 * Loads the given [inputStream] to the [localDirectory] by the given [path].
 */
class LazyLocalEnvironmentFile(override val path: Path,
                               private val localDirectory: Path,
                               private val inputStream: InputStream
) : LocalFile {

    override val content: String by lazy(LazyThreadSafetyMode.NONE) {
        Files.readAllLines(localPath).joinToString("\n")
    }

    override val localPath: Path by lazy {
        localDirectory
                .resolve(path)
                .also {
                    Files.createDirectories(it.parent)
                    Files.copy(inputStream, it)
                }
    }

}