package org.flaxo.core.env.file

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

    fun toLocalFile(directory: Path): LocalFile = throw UnsupportedOperationException(
            "Instances of ${this::class.simpleName} can't be transformed to a local file."
    )

}