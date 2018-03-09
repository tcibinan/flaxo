package com.tcibinan.flaxo.moss

/**
 * Plagiarism analysis match data object.
 */
data class MossMatch(
    val students: Pair<String, String>,
    val lines: Int,
    val link: String,
    val percentage: Int
)