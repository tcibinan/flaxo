package org.flaxo.frontend.component.report

import org.flaxo.common.data.DateTime
import org.flaxo.common.data.PlagiarismMatch
import org.flaxo.common.data.PlagiarismReport
import org.flaxo.common.data.Solution
import react.RBuilder
import react.dom.i
import react.dom.span

private const val PLAGIARISM_THRESHOLD = 75

fun RBuilder.plagiarismReport(plagiarismReport: PlagiarismReport?, solution: Solution) {
    plagiarismReport?.takeIf { solution.commits.any() }
            ?.also { report ->
                val matches = report.matches.filter { match -> solution.student in match.students }
                val reportIsPositive = matches.all { it.percentage < PLAGIARISM_THRESHOLD }
                val highestMatchPercentage = matches.map { it.percentage }.max()

                if (latestCommitDatetime(solution) >= report.date) {
                    span(classes = "outdated-plagiarism-report") {
                        i(classes = "material-icons plagiarism-marker") { +"visibility_off" }
                    }
                } else {
                    if (matches.isNotEmpty()) {
                        span(classes = if (reportIsPositive) "valid-plagiarism-report" else "invalid-plagiarism-report") {
                            +"${matches.size}  "
                            i(classes = "material-icons plagiarism-marker") { +"remove_red_eye" }
                            +" ($highestMatchPercentage%)"
                        }
                    } else {
                        span(classes = "valid-plagiarism-report") {
                            i(classes = "material-icons plagiarism-marker") { +"visibility_off" }
                        }
                    }
                }
            }
            ?: i {}
}

private fun latestCommitDatetime(solution: Solution): DateTime =
        solution.commits.maxBy { it.date ?: DateTime.min() } ?.date ?: DateTime.min()

private val PlagiarismMatch.students: List<String>
    get() = listOf(student1, student2)
