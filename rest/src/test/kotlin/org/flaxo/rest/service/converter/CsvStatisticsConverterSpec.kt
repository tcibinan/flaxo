package org.flaxo.rest.service.converter

import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

object CsvStatisticsConverterSpec : SubjectSpek<StatisticsConverter>({

    subject { CsvStatisticsConverter }

    describe("statistics converter") {

        on("passing empty statistics") {
            val statistics: Statistics = emptyMap()

            it("should return empty table") {
                subject.convert(statistics)
                        .shouldEqual("""
                            student,score
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
                            student,task-1,task-2,score
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
                            student,task-1,score
                            student-1,10,10
                            student-2,20,20
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
                            student,task-1,task-2,score
                            student-1,10,30,20
                            student-2,40,60,50
                        """.collapse())
            }
        }
    }
})

private fun String.collapse(): String = replace(" ", "").replaceFirst("\n", "")
