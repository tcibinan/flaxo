package org.flaxo.moss

import org.apache.logging.log4j.LogManager
import org.flaxo.core.deleteDirectoryRecursively
import org.flaxo.core.lang.Language
import org.jsoup.Connection
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.net.URL
import java.nio.file.Files

/**
 * Simple Moss submissions analyser implementation.
 */
class SimpleMossSubmissionsAnalyser(private val mossSupplier: (Language) -> Moss,
                                    private val connectionSupplier: (String) -> Connection
) : MossSubmissionAnalyser {

    companion object {
        private val logger = LogManager.getLogger(SimpleMossSubmissionsAnalyser::class.java)
    }

    override fun analyse(submission: MossSubmission): MossResult {
        logger.info("Starting Moss submission ${submission.friendlyId} analysis for " +
                "${submission.base.size} bases files " +
                "and ${submission.solutions.size} solutions files")

        val resultURL = mossSupplier(submission.language)
                .submit(submission)

        val mossResult = MossResult(resultURL, students = submission.students, matches = retrieveMatches(resultURL))

        logger.info("Moss submission ${submission.friendlyId} analysis has finished successfully " +
                "and is available by ${mossResult.url}")

        logger.info("Deleting moss submission ${submission.friendlyId} generated files")

        deleteDirectoryRecursively(submission.tempDirectory)

        return mossResult
    }

    private fun retrieveMatches(mossResultUrl: URL): Set<MossMatch> =
            connectionSupplier(mossResultUrl.toString())
                    .get()
                    .body()
                    .getElementsByTag("table")
                    .select("tr")
                    .drop(1)
                    .map { tr -> tr.select("td") }
                    .map { tds ->
                        val first = tds.first().selectFirst("a")
                        val second = tds.second().selectFirst("a")

                        val link = first.attr("href")

                        val firstPath = first.text().split(" ").first().split("/")
                        val secondPath = second.text().split(" ").first().split("/")

                        val students =
                                firstPath.zip(secondPath)
                                        .dropWhile { it.first == it.second }
                                        .first()

                        val lines = tds.third().text().toInt()

                        val percentage = first.text().split(" ")
                                .last()
                                .removeSurrounding("(", "%)")
                                .toInt()

                        MossMatch(students, lines, link, percentage)
                    }
                    .toSet()

    private fun Elements.second(): Element = get(1)
    private fun Elements.third(): Element = get(2)

}
