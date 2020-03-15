package org.flaxo.frontend.component.report

import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.flaxo.common.data.BuildReport
import org.flaxo.common.data.CodeStyleGrade
import org.flaxo.common.data.CodeStyleReport
import org.flaxo.common.data.Commit
import org.flaxo.common.data.DateTime
import org.flaxo.common.data.Solution
import org.flaxo.common.data.Task
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.dom.button
import react.dom.defaultValue
import react.dom.div
import react.dom.input
import kotlin.browser.document

fun RBuilder.scoreInput(task: Task,
                        solution: Solution,
                        onStudentScoreUpdate: (String, Int?) -> Unit
) {
    val buildReport = solution.buildReports.lastOrNull()
    val codeStyleReport = solution.codeStyleReports.lastOrNull()
    val latestCommit = solution.commits.lastOrNull()
    val suggestedScore = suggestScore(buildReport, codeStyleReport, latestCommit, task.deadline)
    val scoreInputId = "scoreInput${solution.task}${solution.student}"
    val suggestedScoreAppendId = "suggestedScore${solution.task}${solution.student}"

    div(classes = "input-group input-group-sm score-input-container") {
        if (solution.commits.isNotEmpty()) {
            input(classes = "form-control score-input", type = InputType.number) {
                attrs {
                    id = scoreInputId
                    onChangeFunction = { event ->
                        val target = event.target as HTMLInputElement
                        onStudentScoreUpdate(solution.student, target.value.toIntOrNull())
                    }
                    min = "0"
                    max = "100"
                    defaultValue = solution.score?.toString() ?: ""
                    attributes["aria-describedby"] = suggestedScoreAppendId
                    disabled = solution.commits.isEmpty()
                }
            }
            div(classes = "input-group-append") {
                button(classes = "btn btn-outline-info score-input-suggestion") {
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
    val codeStyleGradeScore = codeStyleReport?.grade
            ?.let { codeStyleGradeScore(it) }
            ?: 0
    val deadlinePassed = deadline
            ?.let { (latestCommit.date ?: it) < it }
            ?: true
    return 60 + 5 * codeStyleGradeScore + 10 * deadlinePassed.toInt()
}

private fun Boolean.toInt(): Int = if (this) 1 else 0

fun codeStyleGradeScore(grade: CodeStyleGrade): Int = when (grade) {
    CodeStyleGrade.A -> 6
    CodeStyleGrade.B -> 5
    CodeStyleGrade.C -> 4
    CodeStyleGrade.D -> 3
    CodeStyleGrade.E -> 2
    CodeStyleGrade.F -> 1
    else -> 0
}
