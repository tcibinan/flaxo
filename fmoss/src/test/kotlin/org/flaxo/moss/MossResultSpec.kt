package org.flaxo.moss

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeBlank
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.File
import java.net.URL

object MossResultSpec : SubjectSpek<MossResult>({

    val mossResultsUrl = URL("http://test.url.com/results/2312432")

    val mossAnswerHtml =
            File("src/test/resources/moss-results.html")
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
                matches.size shouldEqual 1
            }

            it("should return matches with students nicknames") {
                matches.map { it.students }.toSet() shouldEqual setOf(Pair("student1", "student3"))
            }

            it("should return matches with counts of lines matched") {
                matches.map { it.lines }.toSet() shouldEqual setOf(9)
            }

            it("should return matches with percentages of matching") {
                matches.map { it.percentage }.toSet() shouldEqual setOf(97)
            }

            it("should return matches with non-blank links to explicit information") {
                matches.map { it.link }
                        .forEach { it.shouldNotBeBlank() }
            }
        }
    }
})