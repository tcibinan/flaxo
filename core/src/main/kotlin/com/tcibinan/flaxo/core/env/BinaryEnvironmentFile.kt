package com.tcibinan.flaxo.core.env

/**
 * Binary file class.
 */
class BinaryEnvironmentFile(private val name: String,
                            private val bytes: ByteArray
) : EnvironmentFile {

    override fun name() = name

    override fun binaryContent() = bytes

    override fun with(path: String): EnvironmentFile =
            BinaryEnvironmentFile(path, bytes)

}
