package org.flaxo.moss.model

import java.time.LocalDateTime

data class AnalysisResultPair(
        val id: Long,
        val student1: String,
        val student2: String,
        val percentage: Int,
        val minPercentage: Int,
        val maxPercentage: Int,
        val createdAt1: LocalDateTime,
        val createdAt2: LocalDateTime
)