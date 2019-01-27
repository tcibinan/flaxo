package org.flaxo.common.env.file

import java.nio.file.Path

/**
 * Local environment file.
 */
interface LocalFile: EnvironmentFile {
    val localPath: Path
}