package org.flaxo.moss.model

data class AnalysisResult(
        val id: Long,
        val resultLink: String,
        val analysisPairs: List<AnalysisResultPair>
)