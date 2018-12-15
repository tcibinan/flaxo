package org.flaxo.moss

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeBlank
import org.flaxo.core.lang.JavaLang
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

object MossSubmissionsAnalyserSpec : SubjectSpek<MossSubmissionAnalyser>({

    val mossResultsUrl = URL("http://test.url.com/results/2312432")
    val mossAnswerHtml = Files.readAllLines(Paths.get("src/test/resources/moss-results.html")).joinToString("\n")
    val submission = MossSubmission("user", "course", "branch", JavaLang, emptyList(), emptyList())

    val moss: Moss = mock {
        on { submit(any()) }.thenReturn(mossResultsUrl)
    }

    val connection: Connection = mock {
        on { get() }.thenReturn(Jsoup.parse(mossAnswerHtml))
    }

    subject {
        SimpleMossSubmissionsAnalyser({ moss }, { connection })
    }

    describe("Moss submissions analyser") {

        on("analysing submission") {
            val result = subject.analyse(submission)

            it("should return a result with all matches") {
                result.matches.size shouldEqual 1
            }

            it("should return a result with students nicknames") {
                result.matches.map { it.students }.toSet() shouldEqual setOf(Pair("student1", "student3"))
            }

            it("should return a result with counts of lines matched") {
                result.matches.map { it.lines }.toSet() shouldEqual setOf(9)
            }

            it("should return a result with percentages of matching") {
                result.matches.map { it.percentage }.toSet() shouldEqual setOf(97)
            }

            it("should return a result with non-blank links to explicit information") {
                result.matches.map { it.link }
                        .forEach { it.shouldNotBeBlank() }
            }

            it("should return a result with correct result URL") {
                result.url shouldEqual mossResultsUrl
            }
        }
    }
})
