package org.flaxo.core.env

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Binary file class.
 */
class BinaryEnvironmentFile(name: String,
                            override val binaryContent: ByteArray
) : EnvironmentFile {

    override val path: Path = Paths.get(name)

}
