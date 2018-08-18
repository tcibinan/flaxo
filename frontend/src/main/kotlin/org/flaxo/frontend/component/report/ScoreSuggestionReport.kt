package org.flaxo.frontend.component.report

import kotlinx.html.InputType
import kotlinx.html.id
import org.flaxo.frontend.data.BuildReport
import org.flaxo.frontend.data.CodeStyleReport
import org.flaxo.frontend.data.Commit
import org.flaxo.frontend.data.Solution
import org.flaxo.frontend.data.Task
import react.RBuilder
import react.dom.button
import react.dom.div
import react.dom.input
import kotlin.js.Date

fun RBuilder.scoreInput(task: Task, solution: Solution) {
    val buildReport = solution.buildReports.lastOrNull()
    val codeStyleReport = solution.codeStyleReports.lastOrNull()
    val latestCommit = solution.commits.lastOrNull()
    val suggestedScore = suggestScore(buildReport, codeStyleReport, latestCommit, task.deadline).toString()
    val suggestedScoreAppendId = "suggestedScore${solution.task}${solution.student}"

    div(classes = "input-group") {
        input(classes = "form-control", type = InputType.number) {
            attrs {
                attributes["aria-describedby"] = suggestedScoreAppendId
            }
        }
        div(classes = "input-group-append") {
            button(classes = "btn btn-outline-info") {
                // TODO 18.08.18: Activate bootstrap popovers support
                attrs {
                    id = suggestedScoreAppendId
                    attributes["data-toggle"] = "popover"
                    attributes["title"] = "Suggested score"
                    attributes["data-content"] = "Student: ${solution.student}.\nScore: $suggestedScore"
                    attributes["data-placement"] = "bottom"
                }
                +suggestedScore
            }
        }
    }
}

fun suggestScore(buildReport: BuildReport?,
                 codeStyleReport: CodeStyleReport?,
                 latestCommit: Commit?,
                 deadline: Date?
): Int {
    if (latestCommit == null
            || buildReport == null
            || !buildReport.succeed) return 0
    val codeStyleGrade = codeStyleReport?.grade
            ?.let { grade -> CodeStyleGrade.values().find { it.name == grade } }
            ?: CodeStyleGrade.NONE
    val deadlinePassed = deadline
            ?.let { (latestCommit.date ?: it) < it }
            ?: true
    return 60 + 5 * codeStyleGrade.score + 10 * deadlinePassed.toInt()
}

private fun Boolean.toInt(): Int = if (this) 1 else 0

enum class CodeStyleGrade(val score: Int) {
    A(6),
    B(5),
    C(4),
    D(3),
    E(2),
    F(1),
    NONE(0)
}
