package org.flaxo.common.env.file

import java.nio.file.Path
import java.nio.file.Paths

/**
 * In-memory binary environment file.
 */
class ByteArrayEnvironmentFile(override var path: Path,
                               override val binaryContent: ByteArray
) : EnvironmentFile {

    constructor(path: String, binaryContent: ByteArray) : this(Paths.get(path), binaryContent)

}
