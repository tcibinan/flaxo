package com.tcibinan.flaxo.core.env

interface Environment {
    fun getFiles(): Set<File>
}

interface File {
    fun name(): String
    fun content(): String
}

class SimpleFile(
        val name: String,
        val content: String
) : File {
    override fun name() = name
    override fun content() = content
}