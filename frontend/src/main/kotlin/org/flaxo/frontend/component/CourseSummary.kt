package org.flaxo.frontend.component

import kotlinx.html.TABLE
import kotlinx.html.TR
import kotlinx.html.ThScope
import kotlinx.html.js.onClickFunction
import org.flaxo.common.data.Course
import org.flaxo.common.data.CourseStatistics
import org.flaxo.common.data.Solution
import org.flaxo.frontend.github.githubProfileUrl
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.RDOMBuilder
import react.dom.a
import react.dom.button
import react.dom.div
import react.dom.h5
import react.dom.table
import react.dom.tbody
import react.dom.td
import react.dom.th
import react.dom.thead
import react.dom.tr

/**
 * Adds course summary.
 */
fun RBuilder.courseSummary(course: Course, courseStatistics: CourseStatistics) =
        child(CourseSummary::class) {
            attrs {
                this.course = course
                this.courseStatistics = courseStatistics
            }
        }

private class CourseSummaryProps(var course: Course, var courseStatistics: CourseStatistics) : RProps

private class CourseSummary(props: CourseSummaryProps) : RComponent<CourseSummaryProps, EmptyState>(props) {

    override fun RBuilder.render() {
        div(classes = "course-summary") {
            div(classes = "card") {
                div(classes = "card-body") {
                    h5(classes = "card-title") { +"Course summary" }
                    a(classes = "card-link", href = props.course.url) { +"Git repository" }
                    button(classes = "save-results-btn btn btn-outline-primary") {
                        attrs.onClickFunction = { saveScores() }
                        +"Save results"
                    }
                    courseStatisticsRefresh(props.course)
                    div(classes = "course-stats") {
                        table(classes = "table table-sm table-hover") {
                            tableHeader()
                            tableBody()
                        }
                    }

                }
            }
        }
    }

    private fun RDOMBuilder<TABLE>.tableHeader() {
        thead {
            tr {
                th(scope = ThScope.col) { +"#" }
                th(scope = ThScope.col) { +"Student" }
                props.courseStatistics.tasks
                        .map { it.branch }
                        .sorted()
                        .forEach { th(scope = ThScope.col) { +it } }
                th(scope = ThScope.col) { +"Score" }
            }
        }
    }

    private fun RDOMBuilder<TABLE>.tableBody() {
        tbody {
            props.courseStatistics.tasks
                    .flatMap { it.solutions }
                    .groupBy { it.student }
                    .toList()
                    .sortedBy { it.first }
                    .takeIf { it.isNotEmpty() }
                    ?.forEachIndexed { row, (student, solutions) ->
                        tr {
                            studentNumber(row)
                            studentName(student)
                            studentScores(solutions)
                            studentTotalScore(solutions)
                        }
                    }
                    ?: tr {
                        td {
                            attrs {
                                colSpan = (props.courseStatistics.tasks.size + 2).toString()
                            }
                            +"There are no students on the course yet."
                        }
                    }
        }
    }

    private fun RDOMBuilder<TR>.studentNumber(row: Int) =
            th(scope = ThScope.row) { +(row + 1).toString() }

    private fun RDOMBuilder<TR>.studentName(student: String) =
            td {
                a(classes = "github-profile", href = githubProfileUrl(student)) {
                    +student
                }
            }

    private fun RDOMBuilder<TR>.studentScores(solutions: List<Solution>) =
            props.courseStatistics.tasks
                    .map { it.branch }
                    .sorted()
                    .map { task -> solutions.find { it.task == task } }
                    .forEach { solution ->
                        solution?.also { td { +(it.score ?: 0).toString() } }
                                ?: td {}
                    }

    private fun RDOMBuilder<TR>.studentTotalScore(solutions: List<Solution>) =
            td {
                +solutions.map { it.score }
                        .map { it ?: 0 }
                        .map { it.toDouble() }
                        .let { scores -> (scores.sum() / (scores.size * 100)) * 100 }
                        .toInt()
                        .toString()
            }
}

private fun saveScores() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

