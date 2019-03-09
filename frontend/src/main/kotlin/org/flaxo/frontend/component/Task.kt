package org.flaxo.frontend.component

import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import kotlinx.html.ButtonType
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.flaxo.common.DateTime
import org.flaxo.frontend.Container
import org.flaxo.frontend.credentials
import org.flaxo.common.data.Course
import org.flaxo.common.data.CourseLifecycle
import org.flaxo.common.data.SolutionReview
import org.flaxo.common.data.Task
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpException
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.a
import react.dom.button
import react.dom.div
import react.dom.h5
import react.dom.hr
import react.dom.small
import react.setState

/**
 * Adds task.
 */
fun RBuilder.task(course: Course, task: Task) = child(org.flaxo.frontend.component.Task::class) {
    attrs {
        this.course = course
        this.task = task
    }
}

private class TaskProps(var course: Course, var task: Task) : RProps

private class TaskState(var scores: Map<String, Int>, var reviews: Map<String, SolutionReview>) : RState

private class Task(props: TaskProps) : RComponent<TaskProps, TaskState>(props) {

    private companion object {
        // TODO 16.08.18: Id can be non-unique between tabs
        const val RULES_DROPDOWN_ID = "taskRulesDropdown"
    }

    private val flaxoClient: FlaxoClient

    init {
        state.scores = emptyMap()
        state.reviews = emptyMap()
        flaxoClient = Container.flaxoClient
    }

    override fun RBuilder.render() {
        div(classes = "task-card") {
            div(classes = "card") {
                div(classes = "card-body") {
                    h5(classes = "card-title") { +props.task.branch }
                    deadlineIndication()

                    a(classes = "card-link", href = props.task.url) { +"Git branch" }
                    props.task.plagiarismReports
                            .lastOrNull()
                            ?.also { a(classes = "card-link", href = it.url) { +"Plagiarism report" } }
                    button(classes = "btn btn-outline-primary task-btn", type = ButtonType.button) {
                        attrs {
                            onClickFunction = { GlobalScope.launch { analysePlagiarism() } }
                            disabled = !props.course.isRunning()
                                    || !props.task.hasEnoughSolutionsForPlagiarismAnalysis()
                        }
                        +"Analyse plagiarism"
                    }
                    button(classes = "btn btn-outline-primary task-btn") {
                        attrs {
                            onClickFunction = {
                                GlobalScope.launch { saveScores() }
                                GlobalScope.launch { saveApprovals() }
                            }
                            disabled = state.scores.isEmpty() && state.reviews.isEmpty()
                        }
                        +"Save results"
                    }
                    button(classes = "btn btn-outline-secondary task-btn") {
                        attrs {
                            attributes["data-toggle"] = "collapse"
                            attributes["data-target"] = "#$RULES_DROPDOWN_ID"
                            attributes["aria-expanded"] = "false"
                            attributes["aria-controls"] = RULES_DROPDOWN_ID
                        }
                        +"Rules"
                    }
                    div(classes = "collapse") {
                        attrs.id = RULES_DROPDOWN_ID
                        hr {}
                        rules(props.course, props.task)
                    }
                    taskStatistics(props.course, props.task,
                            onSolutionScoreUpdate = { student, score ->
                                setState { scores += Pair(student, score) }
                            },
                            onReviewAddition = { student, review ->
                                setState { reviews += Pair(student, review) }
                            }
                    )
                }
            }
        }
    }

    private fun RBuilder.deadlineIndication() {
        props.task.deadline?.also { deadline ->
            val now = DateTime.now()
            small(classes = "text-muted task-deadline") {
                if (now < deadline) {
                    val days = now.daysUntil(deadline)
                    when {
                        days > 1 -> +"Deadline in $days days"
                        days == 1 -> +"Deadline is tomorrow"
                        else -> +"Deadline is today"
                    }
                } else {
                    val days = deadline.daysUntil(now)
                    when {
                        days >= 1 -> +"Deadline was $days days ago"
                        else -> +"Deadline was yesterday"
                    }
                }
            }
        }
    }

    private suspend fun analysePlagiarism() {
        credentials?.also {
            try {
                Notifications.info("Task ${props.task.branch} plagiarism analysis has been started.")
                flaxoClient.analysePlagiarism(it, props.course.name, props.task.branch)
                Notifications.success("Task ${props.course.name} plagiarism analysis has finished successfully.")
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred performing task ${props.task.branch} plagiarism analysis.", e)
            }
        }
    }

    private suspend fun saveScores() {
        credentials
                ?.takeIf { state.scores.isNotEmpty() }
                ?.also { credentials ->
                    try {
                        Container.flaxoClient.updateScores(credentials,
                                courseName = props.course.name,
                                task = props.task.branch,
                                scores = state.scores)
                        setState { scores = emptyMap() }
                        Notifications.success("Task results were saved")
                    } catch (e: FlaxoHttpException) {
                        console.log(e)
                        Notifications.error("Error occurred while saving task results", e)
                    }
                }
    }

    private suspend fun saveApprovals() {
        credentials
                ?.takeIf { state.reviews.isNotEmpty() }
                ?.also { credentials ->
                    try {
                        Container.flaxoClient.updateSolutionApprovals(credentials,
                                courseName = props.course.name,
                                task = props.task.branch,
                                approvals = state.reviews
                        )
                        setState { reviews = emptyMap() }
                        Notifications.success("Task approvals were saved")
                    } catch (e: FlaxoHttpException) {
                        console.log(e)
                        Notifications.error("Error occurred while saving task approvals", e)
                    }
                }
    }

}

private fun Course.isRunning(): Boolean = state.lifecycle == CourseLifecycle.RUNNING

private fun Task.hasEnoughSolutionsForPlagiarismAnalysis(): Boolean =
        solutions.asSequence()
                .map { it.commits }
                .filter { it.isNotEmpty() }
                .toList()
                .size > 1
