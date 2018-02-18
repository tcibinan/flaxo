package com.tcibinan.flaxo.core.env

class BinaryEnvironmentFile(private val name: String,
                            private val bytes: ByteArray
) : EnvironmentFile {

    override fun name() = name

    override fun content() =
            throw UnsupportedOperationException("Binary file doesn't have string content")

    override fun binaryContent() = bytes

}
