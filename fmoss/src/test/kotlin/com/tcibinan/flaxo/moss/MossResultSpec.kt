package com.tcibinan.flaxo.moss

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.File
import java.net.URL
import kotlin.test.assertTrue

object MossResultSpec : SubjectSpek<MossResult>({

    val mossResultsUrl = URL("http://test.url.com/results/2312432")

    val mossAnswerHtml =
            File("fmoss/src/test/resources/moss-results.html")
                    .readLines()
                    .joinToString("\n")

    val connection: Connection = mock {
        on { get() }.thenReturn(Jsoup.parse(mossAnswerHtml))
    }

    val connectionProvider: (String) -> Connection = mock {
        on { invoke(any()) }.thenReturn(connection)
    }

    subject { SimpleMossResult(mossResultsUrl, connectionProvider) }

    describe("Moss analysis result") {

        on("getting matches") {
            val matches = subject.matches()

            it("should return a set with all matches") {
                assertTrue { matches.size == 1 }
            }

            it("should return matches with students nicknames") {
                assertTrue {
                    matches.map { it.students() }
                            .toSet() == setOf(Pair("student1", "student2"))
                }
            }

            it("should return matches with counts of lines matched") {
                assertTrue { matches.map { it.lines() }.toSet() == setOf(9) }
            }

            it("should return matches with percentages of matching") {
                assertTrue { matches.map { it.percentage() }.toSet() == setOf(97) }
            }
        }
    }
})