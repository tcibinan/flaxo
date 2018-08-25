package org.flaxo.frontend.component.report

import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.flaxo.common.BuildReport
import org.flaxo.common.CodeStyleReport
import org.flaxo.common.Commit
import org.flaxo.common.DateTime
import org.flaxo.common.Solution
import org.flaxo.common.Task
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.dom.button
import react.dom.defaultValue
import react.dom.div
import react.dom.input
import kotlin.browser.document

fun RBuilder.scoreInput(task: Task,
                        solution: Solution,
                        onStudentScoreUpdate: (String, Int) -> Unit
) {
    val buildReport = solution.buildReports.lastOrNull()
    val codeStyleReport = solution.codeStyleReports.lastOrNull()
    val latestCommit = solution.commits.lastOrNull()
    val suggestedScore = suggestScore(buildReport, codeStyleReport, latestCommit, task.deadline)
    val scoreInputId = "scoreInput${solution.task}${solution.student}"
    val suggestedScoreAppendId = "suggestedScore${solution.task}${solution.student}"

    div(classes = "input-group input-group-sm") {
        input(classes = "form-control", type = InputType.number) {
            attrs {
                id = scoreInputId
                onChangeFunction = { event ->
                    val target = event.target as HTMLInputElement
                    onStudentScoreUpdate(solution.student, target.value.toIntOrNull() ?: 0)
                }
                min = "0"
                max = "100"
                defaultValue = solution.score?.toString() ?: ""
                attributes["aria-describedby"] = suggestedScoreAppendId
                disabled = solution.commits.isEmpty()
            }
        }
        if (solution.commits.isNotEmpty()) {
            div(classes = "input-group-append") {
                button(classes = "btn btn-outline-info") {
                    attrs {
                        id = suggestedScoreAppendId
                        onClickFunction = { _ ->
                            document.getElementById(scoreInputId)
                                    ?.let { it as? HTMLInputElement }
                                    ?.value = suggestedScore.toString()
                            onStudentScoreUpdate(solution.student, suggestedScore)
                        }
                    }
                    +suggestedScore.toString()
                }
            }
        }
    }
}

fun suggestScore(buildReport: BuildReport?,
                 codeStyleReport: CodeStyleReport?,
                 latestCommit: Commit?,
                 deadline: DateTime?
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
