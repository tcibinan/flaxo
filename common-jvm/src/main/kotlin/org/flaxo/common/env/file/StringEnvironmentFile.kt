package org.flaxo.common.env.file

import java.nio.file.Path
import java.nio.file.Paths

/**
 * In-memory string environment file.
 */
open class StringEnvironmentFile(override val path: Path, override val content: String) : EnvironmentFile {

    constructor(path: String, content: String) : this(Paths.get(path), content)

    override fun toLocalFile(directory: Path): LocalFile = LazyLocalEnvironmentFile(path, directory) {
        content.byteInputStream()
    }
}
