package org.flaxo.common.env.file

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class LocalEnvironmentFile(override val localPath: Path,
                           override val path: Path = localPath
) : LocalFile {

    constructor(localPath: String) : this(Paths.get(localPath))

    override val content: String by lazy(LazyThreadSafetyMode.NONE) {
        Files.readAllLines(localPath).joinToString("\n")
    }

    override fun toLocalFile(directory: Path): LocalFile {
        val newLocalPath = directory.resolve(path)
        Files.createDirectories(newLocalPath.parent)
        Files.copy(localPath, newLocalPath)
        return LocalEnvironmentFile(newLocalPath, path)
    }
}
