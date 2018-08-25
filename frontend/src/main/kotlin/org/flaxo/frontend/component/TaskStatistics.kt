package org.flaxo.frontend.component

import kotlinx.html.ThScope
import org.flaxo.frontend.component.report.buildReport
import org.flaxo.frontend.component.report.codeStyleReport
import org.flaxo.frontend.component.report.deadlineReport
import org.flaxo.frontend.component.report.plagiarismReport
import org.flaxo.frontend.component.report.scoreInput
import org.flaxo.frontend.data.Course
import org.flaxo.frontend.data.Task
import org.flaxo.frontend.githubPullRequestUrl
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.a
import react.dom.div
import react.dom.table
import react.dom.tbody
import react.dom.td
import react.dom.th
import react.dom.thead
import react.dom.tr

fun RBuilder.taskStatistics(course: Course, task: Task) = child(TaskStatistics::class) {
    attrs {
        this.course = course
        this.task = task
    }
}

class TaskStatisticsProps(var course: Course,
                          var task: Task) : RProps

class TaskStatistics(props: TaskStatisticsProps) : RComponent<TaskStatisticsProps, EmptyState>(props) {

    override fun RBuilder.render() {
        div(classes = "task-results") {
            table(classes = "table table-sm table-hover") {
                thead {
                    tr {
                        th(scope = ThScope.col) { +"#" }
                        th(scope = ThScope.col) { +"Student" }
                        th(scope = ThScope.col) { +"Build" }
                        th(scope = ThScope.col) { +"Code style" }
                        th(scope = ThScope.col) { +"Plagiarism" }
                        th(scope = ThScope.col) { +"Deadline" }
                        th(scope = ThScope.col) { +"Result" }
                    }
                }
                tbody {
                    props.course
                            .students
                            .sorted()
                            .mapNotNull { student -> props.task.solutions.find { it.student == student } }
                            .takeIf { it.isNotEmpty() }
                            ?.forEachIndexed { row, solution ->
                                tr {
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
                                        scoreInput(props.task, solution)
                                    }
                                }
                            }
                            ?: tr {
                                td {
                                    attrs { colSpan = "7" }
                                    +"There are no students on the course yet."
                                }
                            }
                }

            }
        }
    }

}
