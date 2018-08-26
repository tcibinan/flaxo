package org.flaxo.rest.service.converter

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Course statistics json converter.
 */
object JsonStatisticsConverter : StatisticsConverter {

    override val extension: String = "json"

    private val objectMapper by lazy { ObjectMapper() }

    override fun convert(statistics: Map<String, Map<String, Int>>): String {
        val studentSummaryScores: Map<String, Int> = statistics.values
                .flatMap { map -> map.toList() }
                .groupingBy { it.first }
                .aggregate<Pair<String, Int>, String, Int> { _, summaryScore, (_, score), _ ->
                    summaryScore?.plus(score) ?: score
                }
                .mapValues { it.value / statistics.values.size }

        return statistics
                .plus("score" to studentSummaryScores)
                .let { objectMapper.writeValueAsString(it) }
    }

}