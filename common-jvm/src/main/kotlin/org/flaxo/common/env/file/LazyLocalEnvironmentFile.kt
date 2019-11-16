package org.flaxo.common.env.file

import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

/**
 * Lazy local environment file.
 *
 * Loads an input stream retrieved from [supplier] to [localDirectory] by the given relative [path].
 */
class LazyLocalEnvironmentFile(override val path: Path,
                               private val localDirectory: Path,
                               private val supplier: () -> InputStream
) : LocalFile {

    override val binaryContent: ByteArray by lazy { Files.readAllBytes(localPath) }

    override val localPath: Path by lazy {
        localDirectory.resolve(path).also {
            Files.createDirectories(it.parent)
            Files.copy(supplier(), it)
        }
    }

    override fun toLocalFile(directory: Path): LocalFile = LazyLocalEnvironmentFile(path, directory, supplier)

    override fun flush(): LocalFile = apply { localPath }
}
