package org.flaxo.common.env.file

import java.nio.file.Path

/**
 * Environment file.
 *
 * **Interface contract:** At least on of the methods [content] or [binaryContent] should be overridden
 * otherwise [StackOverflowError] will be thrown on calling one of these methods.
 */
interface EnvironmentFile  {

    /**
     * Relative path of the file in the environment.
     */
    val path: Path

    /**
     * Environment file name with extension.
     */
    val fileName: String
        get() = path.fileName.toString()

    /**
     * File string content.
     */
    val content: String
        get() = String(binaryContent)

    /**
     * File binary content.
     */
    val binaryContent: ByteArray
        get() = content.toByteArray()

    /**
     * Converts environment file to a local file.
     */
    fun toLocalFile(directory: Path): LocalFile

}
