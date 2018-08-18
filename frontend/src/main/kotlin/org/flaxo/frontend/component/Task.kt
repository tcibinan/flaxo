package org.flaxo.frontend.component

import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.data.Course
import org.flaxo.frontend.data.Task
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.a
import react.dom.button
import react.dom.div
import react.dom.h5

fun RBuilder.task(course: Course, task: Task) = child(org.flaxo.frontend.component.Task::class) {
    attrs {
        this.course = course
        this.task = task
    }
}

class TaskProps(var course: Course,
                var task: Task) : RProps

class Task(props: TaskProps) : RComponent<TaskProps, EmptyState>(props) {

    private companion object {
        // TODO 16.08.18: Id can be non-unique between tabs
        val RULES_DROPDOWN_ID = "taskRulesDropdown"
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
                            ?: a(classes = "card-link") {
                                attrs { attributes["disabled"] = "true" }
                                +"Plagiarism report"
                            }
                    button(classes = "save-results-btn btn btn-outline-primary") {
                        attrs { onClickFunction = { saveResults() } }
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
                        rules(props.course, props.task)
                    }
                    taskStatistics(props.course, props.task)
                }
            }
        }
    }

    private fun saveResults() {
        //todo: Implement results aggregation and saving
    }

}
