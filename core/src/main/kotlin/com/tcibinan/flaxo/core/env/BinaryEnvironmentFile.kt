package com.tcibinan.flaxo.core.env

/**
 * Binary file class.
 */
class BinaryEnvironmentFile(override val name: String,
                            private val bytes: ByteArray
) : EnvironmentFile {

    override fun binaryContent() = bytes

    override fun with(path: String): EnvironmentFile =
            BinaryEnvironmentFile(path, bytes)

}
