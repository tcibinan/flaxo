package com.tcibinan.flaxo.core.env

import java.io.File

/**
 * Repository file.
 *
 * It could be task, solution, doc file in string or binary representation.
 *
 * Sometimes there is need to close and free resources so the proper way to
 * use [EnvironmentFile] is to use it as a typical [AutoCloseable] resource.
 */
interface EnvironmentFile: AutoCloseable {

    /**
     * @return Full path of the environment file.
     */
    fun name(): String

    /**
     * @return String representation of the environment file.
     * @throws UnsupportedOperationException if the environment file doesn't have string representation.
     */
    fun content(): String =
            throw UnsupportedOperationException(
                    "There is no String representation for " +
                            "the instances of ${this::class.simpleName}"
            )

    /**
     * @return binary content of the environment file.
     */
    fun binaryContent(): ByteArray = content().toByteArray()

    /**
     * @return java.io.file representation of the environment file.
     * @throws UnsupportedOperationException if the environment file doesn't have java.io.file representation.
     */
    fun file(): File =
            throw UnsupportedOperationException(
                    "There is no java.io.File representation for " +
                            "the instances of ${this::class.simpleName}"
            )

    /**
     * @return an environment file with the new [path] and the same content.
     */
    fun with(path: String): EnvironmentFile

    override fun close() {
        // There is nothing to close by default
    }

}