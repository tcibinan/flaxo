package org.flaxo.frontend.component

import kotlinx.coroutines.experimental.launch
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.credentials
import org.flaxo.common.Course
import org.flaxo.common.Task
import org.flaxo.frontend.Notifications
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

fun RBuilder.task(course: Course, task: Task) = child(org.flaxo.frontend.component.Task::class) {
    attrs {
        this.course = course
        this.task = task
    }
}

class TaskProps(var course: Course,
                var task: Task) : RProps

class TaskState(var scores: MutableMap<String, Int>) : RState

class Task(props: TaskProps) : RComponent<TaskProps, TaskState>(props) {

    private companion object {
        // TODO 16.08.18: Id can be non-unique between tabs
        val RULES_DROPDOWN_ID = "taskRulesDropdown"
    }

    init {
        state.scores = mutableMapOf()
    }

    override fun RBuilder.render() {
        div(classes = "task-card") {
            div(classes = "card") {
                div(classes = "card-body") {
                    h5(classes = "card-title") { +props.task.branch }
                    a(classes = "card-link", href = props.task.url) { +"Git branch" }
                    props.task.plagiarismReports
                            .lastOrNull()
                            ?.also { a(classes = "card-link", href = it.url) { +"Plagiarism report" } }
                    button(classes = "save-results-btn btn btn-outline-primary") {
                        attrs { onClickFunction = { launch { saveResults() } } }
                        +"Save results"
                    }
                    button(classes = "rules-toggle-btn btn btn-outline-secondary") {
                        attrs {
                            attributes["data-toggle"] = "collapse"
                            attributes["data-target"] = "#$RULES_DROPDOWN_ID"
                            attributes["aria-expanded"] = "false"
                            attributes["aria-controls"] = RULES_DROPDOWN_ID
                        }
                        +"Rules"
                    }
                    div(classes = "collapse") {
                        attrs { id = RULES_DROPDOWN_ID }
                        hr {}
                        rules(props.course, props.task)
                    }
                    taskStatistics(props.course, props.task, onStudentScoreUpdate = ::updateStudentScore)
                }
            }
        }
    }

    private suspend fun saveResults() {
        credentials?.also { credentials ->
            try {
                Container.flaxoClient.updateScores(credentials,
                        courseName = props.course.name,
                        task = props.task.branch,
                        scores = state.scores)
                Notifications.success("Task results were saved")
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred while saving task results", e)
            }
        }
    }

    private fun updateStudentScore(student: String, score: Int) {
        state.scores[student] = score
    }

}
