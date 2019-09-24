package org.flaxo.moss.model

data class AnalysisRequest(
        val branch: String,
        val analyzer: String = "jplag",
        val language: String,
        val updateFiles: Boolean = false
)
