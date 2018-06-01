package org.flaxo.rest.service.converter

import java.util.*

object CsvStatisticsConverter : StatisticsConverter {

    private const val delimiter = ","

    override val extension: String = "csv"

    override fun convert(statistics: Map<String, Map<String, Int>>): String {
        val csvBuilder = StringBuilder()

        csvBuilder.append("student").append(delimiter)
        statistics.keys.sorted()
                .fold(StringJoiner(delimiter)) { joiner, task -> joiner.add(task) }
                .also { csvBuilder.append(it).append(delimiter) }
        csvBuilder.append("score").append("\n")

        val studentsStatistics: Map<String, List<Int>> =
                statistics
                        .flatMap { (task, studentScore) ->
                            studentScore.map { (student, score) -> Triple(task, student, score) }
                        }
                        .sortedBy { it.first }
                        .groupingBy { it.second }
                        .aggregate { _, tasks, triple, _ ->
                            tasks?.plus(triple.third) ?: listOf(triple.third)
                        }
        studentsStatistics.forEach { student, scores ->
            csvBuilder.append(student).append(delimiter)
            scores.toList().fold(StringJoiner(delimiter)) { joiner, score -> joiner.add(score.toString()) }
                    .also { csvBuilder.append(it).append(delimiter) }
            csvBuilder.append(scores.average().toInt()).append("\n")
        }

        return csvBuilder.toString()
    }

}