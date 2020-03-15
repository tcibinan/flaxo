package org.flaxo.frontend.component

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.ButtonType
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.flaxo.common.data.Course
import org.flaxo.common.data.CourseLifecycle
import org.flaxo.common.data.DateTime
import org.flaxo.common.data.PlagiarismReport
import org.flaxo.common.data.SolutionReview
import org.flaxo.common.data.Task
import org.flaxo.frontend.Configuration
import org.flaxo.frontend.Container
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.OnTaskChange
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpException
import org.flaxo.frontend.credentials
import org.w3c.dom.HTMLIFrameElement
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
import kotlin.browser.document

/**
 * Adds task.
 */
fun RBuilder.task(course: Course, task: Task, onUpdate: OnTaskChange) = child(TaskComponent::class) {
    attrs {
        this.course = course
        this.task = task
        this.onUpdate = onUpdate
    }
}

private class TaskProps(var course: Course, var task: Task, var onUpdate: OnTaskChange) : RProps

private class TaskState(var scores: Map<String, Int>,
                        var reviews: Map<String, SolutionReview>,
                        var plagiarismReport: PlagiarismReport?) : RState

private class TaskComponent(props: TaskProps) : RComponent<TaskProps, TaskState>(props) {

    private companion object {
        // TODO 16.08.18: Id can be non-unique between tabs
        const val RULES_DROPDOWN_ID = "taskRulesDropdown"
    }

    private val flaxoClient: FlaxoClient

    init {
        state.scores = emptyMap()
        state.reviews = emptyMap()
        state.plagiarismReport = props.task.plagiarismReports.maxBy { it.date }
        flaxoClient = Container.flaxoClient
    }

    override fun RBuilder.render() {
        div(classes = "task-card") {
            div(classes = "card") {
                div(classes = "card-body") {
                    h5(classes = "card-title") { +props.task.branch }
                    taskStatus()
                    div(classes = "card-controls") {
                        a(classes = "card-link", href = props.task.url) { +"Git branch" }
                        state.plagiarismReport
                                ?.also {
                                    a(classes = "card-link", href = it.url) {
                                        +"Plagiarism report"
                                    }
                                    a(classes = "card-link", href = "") {
                                        attrs {
                                            dataToggle = "modal"
                                            dataTarget = "#$PLAGIARISM_MODAL_ID"
                                            onClickFunction = {
                                                GlobalScope.launch { showPlagiarismGraph(props.course, props.task) }
                                            }
                                        }
                                        +"Plagiarism graph"
                                    }
                                }
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
                        button(classes = "btn btn-outline-secondary task-btn separated-control") {
                            attrs {
                                dataToggle = "collapse"
                                dataTarget = "#$RULES_DROPDOWN_ID"
                                attributes["aria-expanded"] = "false"
                                attributes["aria-controls"] = RULES_DROPDOWN_ID
                            }
                            +"Rules"
                        }
                    }
                    div(classes = "collapse") {
                        attrs.id = RULES_DROPDOWN_ID
                        hr {}
                        rules(props.course, props.task, props.onUpdate)
                    }
                    taskStatistics(props.course, props.task,
                            state.plagiarismReport,
                            state.scores,
                            onSolutionScoreUpdate = { student, score ->
                                val solutionScore = props.task.solutions
                                        .filter { it.student == student }
                                        .map { it.score }
                                        .firstOrNull()
                                if (solutionScore != score) {
                                    setState { scores += Pair(student, score ?: 0) }
                                } else {
                                    setState { scores -= student }
                                }
                            },
                            onReviewAddition = { student, review ->
                                setState { reviews += Pair(student, review) }
                            }
                    )
                }
            }
        }
    }

    private fun RBuilder.taskStatus() {
        val now = DateTime.now()
        val deadline = props.task.deadline
        val latestPlagiarismAnalysisDatetime = state.plagiarismReport?.date
        small(classes = "text-muted task-deadline") {
            if (deadline != null) {
                deadlineIndication(now, deadline)
                latestPlagiarismAnalysisDatetime?.also { latestAnalysisDate ->
                    +", "
                    latestPlagiarismAnalysisIndication(now, latestAnalysisDate, heading = false)
                }
            } else {
                latestPlagiarismAnalysisDatetime?.also { latestAnalysisDate ->
                    latestPlagiarismAnalysisIndication(now, latestAnalysisDate)
                }
            }
        }
    }

    private fun RBuilder.deadlineIndication(now: DateTime, date: DateTime) {
        +if (now < date) {
            val days = now.daysUntil(date)
            when {
                days > 1 -> "Deadline in $days days"
                days == 1 -> "Deadline is tomorrow"
                else -> "Deadline is today"
            }
        } else {
            val days = date.daysUntil(now)
            when {
                days >= 1 -> "Deadline was $days days ago"
                else -> "Deadline was yesterday"
            }
        }
    }

    private fun RBuilder.latestPlagiarismAnalysisIndication(now: DateTime,
                                                            date: DateTime,
                                                            heading: Boolean = true) {
        val days = now.daysAfter(date)
        +if (heading) "P" else "p"
        +when {
            days > 1 -> "lagiarism analysis was $days days ago"
            days == 1 -> "lagiarism analysis was yesterday"
            else -> "lagiarism analysis was today"
        }
        if (anyCommitSince(date)) {
            +" and new commits were added since"
        }
    }

    private fun anyCommitSince(date: DateTime): Boolean = props.task.solutions
            .mapNotNull { solution -> solution.commits.maxBy { it.date ?: DateTime.min() } }
            .mapNotNull { commit -> commit.date }
            .any { it > date }

    private suspend fun analysePlagiarism() {
        credentials?.also {
            try {
                Notifications.info("Task ${props.task.branch} plagiarism analysis has been started.")
                val plagiarismReport = flaxoClient.analysePlagiarism(it, props.course.name, props.task.branch)
                setState { this.plagiarismReport = plagiarismReport }
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
                        val solutions = flaxoClient.updateScores(credentials,
                                courseName = props.course.name,
                                task = props.task.branch,
                                scores = state.scores)
                        setState { scores = emptyMap() }
                        props.onUpdate(props.task.copy(solutions = solutions))
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
                        flaxoClient.updateSolutionApprovals(credentials,
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

    private suspend fun showPlagiarismGraph(course: Course, task: Task) {
        credentials?.also { credentials ->
            try {
                val data2GraphUrl = Configuration.DATA2GRAPH_URL
                val restUrl = Configuration.SERVER_URL
                val graphToken = flaxoClient.getPlagiarismGraphAccessToken(credentials, course.name, task.branch)
                document.getElementById(PLAGIARISM_IFRAME_ID)
                        ?.let { it as? HTMLIFrameElement }
                        ?.src = "$data2GraphUrl/?graph_url=$restUrl/plagiarism/graph/$graphToken"
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred while loading plagiarism graph", e)
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
