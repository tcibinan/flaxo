package org.flaxo.rest.service.converter

import com.fasterxml.jackson.databind.ObjectMapper

object JsonStatisticsConverter : StatisticsConverter {

    private val objectMapper by lazy { ObjectMapper() }

    override fun convert(statistics: Map<String, Map<String, Int>>): String =
            objectMapper.writeValueAsString(statistics)

}