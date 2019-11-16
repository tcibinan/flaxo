package org.flaxo.common.env.file

import java.nio.file.Path

/**
 * Local environment file.
 */
interface LocalFile: EnvironmentFile {

    /**
     * Local file system path.
     */
    val localPath: Path

    /**
     * Flush local file to filesystem in case it wasn't localized yet.
     */
    fun flush(): LocalFile
}
