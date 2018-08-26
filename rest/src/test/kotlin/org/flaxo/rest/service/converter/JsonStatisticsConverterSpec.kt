package org.flaxo.rest.service.converter

import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

object JsonStatisticsConverterSpec : SubjectSpek<StatisticsConverter>({

    subject { JsonStatisticsConverter }

    describe("statistics converter") {

        on("passing empty statistics") {
            val statistics: Statistics = emptyMap()

            it("should return empty score object") {
                subject.convert(statistics)
                        .shouldEqual("""
                            {
                                "score": {}
                            }
                        """.collapse())
            }
        }

        on("passing empty tasks statistics") {
            val statistics: Statistics = mapOf(
                    "task-1" to emptyMap(),
                    "task-2" to emptyMap()
            )

            it("should return empty tasks objects and single empty summary score object") {
                subject.convert(statistics)
                        .shouldEqual("""
                            {
                                "task-1": {},
                                "task-2": {},
                                "score": {}
                            }
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

            it("should return equal task and summary score objects") {
                subject.convert(statistics)
                        .shouldEqual("""
                            {
                                "task-1": {
                                    "student-1": 10,
                                    "student-2": 20
                                },
                                "score": {
                                    "student-1": 10,
                                    "student-2": 20
                                }
                            }
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

            it("should return tasks objects and average summary score object") {
                subject.convert(statistics)
                        .shouldEqual("""
                            {
                                "task-1": {
                                    "student-1": 10,
                                    "student-2": 40
                                },
                                "task-2": {
                                    "student-1": 30,
                                    "student-2": 60
                                },
                                "score": {
                                    "student-1": 20,
                                    "student-2": 50
                                }
                            }
                        """.collapse())
            }
        }
    }
})

private val whiteSpaceRegex = Regex("\\s")

private fun String.collapse(): String = replace(whiteSpaceRegex, "")
