package org.flaxo.rest.service.converter

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Course statistics json converter.
 */
object JsonStatisticsConverter : StatisticsConverter {

    override val extension: String = "json"

    private val objectMapper by lazy { ObjectMapper() }

    override fun convert(statistics: Map<String, Map<String, Int>>): String =
            // TODO: 01/06/18 Add course summary score to output file
            objectMapper.writeValueAsString(statistics)

}