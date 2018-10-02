package org.flaxo.frontend.component

import kotlinx.html.TABLE
import kotlinx.html.TR
import kotlinx.html.ThScope
import org.flaxo.frontend.component.report.buildReport
import org.flaxo.frontend.component.report.codeStyleReport
import org.flaxo.frontend.component.report.deadlineReport
import org.flaxo.frontend.component.report.plagiarismReport
import org.flaxo.frontend.component.report.scoreInput
import org.flaxo.common.Course
import org.flaxo.common.Solution
import org.flaxo.common.Task
import org.flaxo.frontend.component.report.approveCheckBox
import org.flaxo.frontend.github.githubPullRequestUrl
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.RDOMBuilder
import react.dom.a
import react.dom.div
import react.dom.table
import react.dom.tbody
import react.dom.td
import react.dom.th
import react.dom.thead
import react.dom.tr

fun RBuilder.taskStatistics(course: Course,
                            task: Task,
                            onSolutionScoreUpdate: (String, Int) -> Unit,
                            onSolutionApprovalUpdate: (String, Boolean) -> Unit
) = child(TaskStatistics::class) {
    attrs {
        this.course = course
        this.task = task
        this.onStudentScoreUpdate = onSolutionScoreUpdate
        this.onStudentSolutionApproval = onSolutionApprovalUpdate
    }
}

class TaskStatisticsProps(var course: Course,
                          var task: Task,
                          var onStudentScoreUpdate: (String, Int) -> Unit,
                          var onStudentSolutionApproval: (String, Boolean) -> Unit
) : RProps

class TaskStatistics(props: TaskStatisticsProps) : RComponent<TaskStatisticsProps, EmptyState>(props) {

    override fun RBuilder.render() {
        div(classes = "task-results") {
            table(classes = "table table-sm table-hover") {
                tableHeader()
                tableBody()
            }
        }
    }

    private fun RDOMBuilder<TABLE>.tableHeader() =
            thead {
                tr {
                    th(scope = ThScope.col) { +"#" }
                    th(scope = ThScope.col) { +"Student" }
                    th(scope = ThScope.col) { +"Build" }
                    th(scope = ThScope.col) { +"Code style" }
                    th(scope = ThScope.col) { +"Plagiarism" }
                    th(scope = ThScope.col) { +"Deadline" }
                    th(scope = ThScope.col) { +"Result" }
                    th(scope = ThScope.col) { +"Approved" }
                }
            }

    private fun RDOMBuilder<TABLE>.tableBody() =
            tbody {
                props.course
                        .students
                        .sorted()
                        .mapNotNull { student -> props.task.solutions.find { it.student == student } }
                        .takeIf { it.isNotEmpty() }
                        ?.forEachIndexed { row, solution ->
                            tr {
                                studentNumberAndName(solution, row)
                                td(classes = "report-cell") {
                                    buildReport(solution)
                                }
                                td(classes = "report-cell") {
                                    codeStyleReport(solution)
                                }
                                td(classes = "report-cell") {
                                    plagiarismReport(props.task, solution)
                                }
                                td(classes = "report-cell") {
                                    deadlineReport(props.task, solution)
                                }
                                td(classes = "report-cell") {
                                    scoreInput(props.task, solution,
                                            onStudentScoreUpdate = props.onStudentScoreUpdate)
                                }
                                td(classes = "report-cell") {
                                    approveCheckBox(props.task, solution,
                                            onSolutionApprovalStatusChange = props.onStudentSolutionApproval)
                                }
                            }
                        }
                        ?: tr {
                            td {
                                attrs { colSpan = "8" }
                                +"There are no students on the course yet."
                            }
                        }
            }

    private fun RDOMBuilder<TR>.studentNumberAndName(solution: Solution, row: Int) =
            if (solution.commits.isEmpty()) {
                th(classes = "text-muted", scope = ThScope.row) { +((row + 1).toString()) }
                td(classes = "text-muted") {
                    a(classes = "github-profile",
                            href = githubPullRequestUrl(props.course, solution)) {
                        +solution.student
                    }
                }
            } else {
                th(scope = ThScope.row) { +((row + 1).toString()) }
                td {
                    a(classes = "github-profile",
                            href = githubPullRequestUrl(props.course, solution)) {
                        +solution.student
                    }
                }
            }
}
