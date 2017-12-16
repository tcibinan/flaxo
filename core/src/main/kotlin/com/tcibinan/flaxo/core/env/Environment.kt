package com.tcibinan.flaxo.core.env

interface Environment {
    fun getFiles(): Set<File>
}

interface File {
    fun name(): String
    fun content(): String
}

class SimpleFile(
        private val name: String,
        private val content: String
) : File {
    override fun name() = name
    override fun content() = content
}