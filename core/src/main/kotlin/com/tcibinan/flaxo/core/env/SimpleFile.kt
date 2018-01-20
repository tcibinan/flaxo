package com.tcibinan.flaxo.core.env

class SimpleFile(
        private val name: String,
        private val content: String
) : File {
    override fun name() = name
    override fun content() = content
}