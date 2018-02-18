package com.tcibinan.flaxo.core.env

interface EnvironmentFile {
    fun name(): String
    fun content(): String
    fun binaryContent(): ByteArray = content().toByteArray()
}