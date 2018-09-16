package org.flaxo.frontend.component.report

import kotlinx.html.js.onClickFunction
import org.flaxo.common.PlagiarismMatch
import org.flaxo.common.Solution
import org.flaxo.common.Task
import org.flaxo.frontend.component.PlagiarismModal
import org.w3c.dom.HTMLIFrameElement
import org.w3c.dom.events.Event
import react.RBuilder
import react.dom.a
import react.dom.i
import react.dom.span
import kotlin.browser.document

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
                        a {
                            attrs {
                                attributes["data-toggle"] = "modal"
                                attributes["data-target"] = "#${PlagiarismModal.PLAGIARISM_MODAL_ID}"
                                onClickFunction = showPlagiarismVisualization(task)
                            }
                            +"${matches.size}  "
                            i(classes = "material-icons plagiarism-marker") { +"remove_red_eye" }
                            +" ($highestMatchPercentage%)"
                        }
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

fun showPlagiarismVisualization(task: Task): (Event) -> Unit = { _ ->
    val students = task.solutions.map { it.student }.toSet()
            .map {
                object {
                    val id = it
                    val group = 1
                }
            }
    val allStudentMatches = task.plagiarismReports.lastOrNull()
            ?.matches
            ?.map {
                object {
                    val source = it.student1
                    val target = it.student2
                    val value = it.percentage
                }
            }
    val graph = object {
        val nodes = students
        val links = allStudentMatches
    }
    val graphJson = JSON.stringify(graph)
    document.getElementById(PlagiarismModal.PLAGIARISM_IFRAME_ID)
            ?.let { it as? HTMLIFrameElement }
            ?.src = "plagiarism/index.html?graph=$graphJson"
}
