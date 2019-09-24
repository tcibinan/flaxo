package org.flaxo.moss.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AnalysisResult(
        val id: Long = -1,
        val resultLink: String = "",
        val analysisPairs: List<AnalysisResultPair> = emptyList()
)