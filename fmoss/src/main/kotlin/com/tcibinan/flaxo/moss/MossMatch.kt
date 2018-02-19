package com.tcibinan.flaxo.moss

interface MossMatch {
    fun students(): Pair<String, String>
    fun lines(): Int
    fun percentage(): Int
}