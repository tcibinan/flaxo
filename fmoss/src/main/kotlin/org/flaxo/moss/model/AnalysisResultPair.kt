package org.flaxo.moss.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AnalysisResultPair(
        val id: Long = -1,
        val student1: String = "",
        val student2: String = "",
        val percentage: Int = -1,
        val minPercentage: Int = -1,
        val maxPercentage: Int = -1
//        ,
//        val createdAt1: LocalDateTime = LocalDateTime.MIN,
//        val createdAt2: LocalDateTime = LocalDateTime.MIN
)