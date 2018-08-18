package org.flaxo.frontend.component.report

import org.flaxo.frontend.data.PlagiarismMatch
import org.flaxo.frontend.data.Solution
import org.flaxo.frontend.data.Task
import react.RBuilder
import react.dom.i
import react.dom.span

private const val PLAGIARISM_THRESHOLD = 80

fun RBuilder.plagiarismReport(task: Task, solution: Solution) {
    task.plagiarismReports
            .lastOrNull()
            ?.takeIf { solution.commits.any() }
            ?.also { report ->
                val matches = report.matches.filter { match -> solution.student in match.students }
                val reportIsPositive = matches.all { it.percentage < PLAGIARISM_THRESHOLD }
                val highestMatchPercentage = matches.map { it.percentage }.max()

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
            ?: i {}
}

private val PlagiarismMatch.students: List<String>
    get() = listOf(student1, student2)