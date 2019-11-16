package org.flaxo.common.env.file

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Local file system environment file.
 */
class LocalEnvironmentFile(override val localPath: Path,
                           override val path: Path = localPath
) : LocalFile {

    constructor(localPath: String) : this(Paths.get(localPath))

    override val binaryContent: ByteArray by lazy { Files.readAllBytes(localPath) }

    override fun toLocalFile(directory: Path): LocalFile = LazyLocalEnvironmentFile(path, directory) {
        Files.newInputStream(localPath)
    }

    override fun flush(): LocalFile = this
}
