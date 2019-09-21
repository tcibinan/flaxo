package org.flaxo.moss.model

data class AnalysisRequest(
        val task: String,
        val analyzer: String = "moss",
        val language: String,
        val updateFiles: Boolean = false
)
