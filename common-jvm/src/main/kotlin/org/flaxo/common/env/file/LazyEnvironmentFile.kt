package org.flaxo.common.env.file

import java.io.InputStream
import java.nio.file.Path

/**
 * Lazy environment file.
 *
 * Loads an input stream retrieved from [supplier] to memory.
 */
class LazyEnvironmentFile(

        override val path: Path,

        private val supplier: () -> InputStream
) : EnvironmentFile {

    override val binaryContent: ByteArray by lazy { supplier().use { it.readBytes() } }

    override fun toLocalFile(directory: Path): LocalFile = LazyLocalEnvironmentFile(path, directory, supplier)
}
