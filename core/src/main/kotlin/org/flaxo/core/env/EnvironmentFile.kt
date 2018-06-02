package org.flaxo.core.env

import java.io.File
import java.nio.file.Path

/**
 * Environment file.
 *
 * It could be task, solution, doc file in string, binary or file representation.
 *
 * Sometimes there is need to close and free resources so the proper way to
 * use [EnvironmentFile] is to use it as a typical [AutoCloseable] resource.
 */
interface EnvironmentFile : AutoCloseable {

    /**
     * Full path of the environment file.
     */
    val path: Path

    val fileName: String
        get() = path.fileName.toString()

    /**
     * String content of the environment file.
     *
     * @throws UnsupportedOperationException if the environment file doesn't have string representation.
     */
    val content: String
        get() = throw UnsupportedOperationException(
                "There is no String representation for " +
                        "the instances of ${this::class.simpleName}"
        )

    /**
     * Binary content of the environment file.
     */
    val binaryContent: ByteArray
        get() = content.toByteArray()

    /**
     * [File] representation of the environment file.
     *
     * @throws UnsupportedOperationException if the environment file doesn't have [File] representation.
     */
    val file: File
        get() = throw UnsupportedOperationException(
                "There is no java.io.File representation for " +
                        "the instances of ${this::class.simpleName}"
        )

    /**
     * @return an environment file copy in the new location by the given [folder].
     */
    fun inFolder(folder: Path): EnvironmentFile =
            throw UnsupportedOperationException(
                    "Operation is not supported for instance of ${this::class.simpleName}"
            )

    override fun close() {
        // There is nothing to close by default
    }

}