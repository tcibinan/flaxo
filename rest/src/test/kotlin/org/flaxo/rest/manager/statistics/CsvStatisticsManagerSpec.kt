package org.flaxo.rest.manager.statistics

import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

object CsvStatisticsManagerSpec : SubjectSpek<CsvStatisticsManager>({
    subject { CsvStatisticsManager() }

    // stands for delimiter
    val d = subject.delimiter

    describe("statistics manager") {

        on("passing empty statistics") {
            val statistics: Statistics = emptyMap()

            it("should return empty table") {
                subject.convert(statistics)
                        .shouldEqual("""
                            student${d}score
                        """.collapse())
            }
        }

        on("passing empty tasks statistics") {
            val statistics: Statistics = mapOf(
                    "task-1" to emptyMap(),
                    "task-2" to emptyMap()
            )

            it("should return empty table with only header") {
                subject.convert(statistics)
                        .shouldEqual("""
                            student${d}task-1${d}task-2${d}score
                        """.collapse())
            }
        }

        on("passing single tasks statistics") {
            val statistics: Statistics = mapOf(
                    "task-1" to mapOf(
                            "student-1" to 10,
                            "student-2" to 20
                    )
            )

            it("should return table with all student records with their task scores and equal summary score") {
                subject.convert(statistics)
                        .shouldEqual("""
                            student${d}task-1${d}score
                            student-1${d}10${d}10
                            student-2${d}20${d}20
                        """.collapse())
            }
        }

        on("passing several tasks statistics") {
            val statistics: Statistics = mapOf(
                    "task-1" to mapOf(
                            "student-1" to 10,
                            "student-2" to 40
                    ),
                    "task-2" to mapOf(
                            "student-1" to 30,
                            "student-2" to 60
                    )
            )

            it("should return table with all student records with their task scores and average summary score") {
                subject.convert(statistics)
                        .shouldEqual("""
                            student${d}task-1${d}task-2${d}score
                            student-1${d}10${d}30${d}20
                            student-2${d}40${d}60${d}50
                        """.collapse())
            }
        }
    }
})

private fun String.collapse(): String = replace(" ", "").replaceFirst("\n", "")
