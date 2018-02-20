package com.tcibinan.flaxo.moss

data class MossMatch(
    val students: Pair<String, String>,
    val lines: Int,
    val link: String,
    val percentage: Int
)