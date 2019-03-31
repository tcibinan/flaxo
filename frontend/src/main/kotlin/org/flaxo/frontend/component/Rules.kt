package org.flaxo.frontend.component

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import org.flaxo.common.data.Course
import org.flaxo.common.data.DateTime
import org.flaxo.common.data.Task
import org.flaxo.frontend.Container
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.OnTaskChange
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpException
import org.flaxo.frontend.credentials
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.button
import react.dom.div
import react.setState

/**
 * Adds task rules menu.
 */
fun RBuilder.rules(course: Course, task: Task, onUpdate: OnTaskChange) = child(Rules::class) {
    attrs {
        this.course = course
        this.task = task
        this.onUpdate = onUpdate
    }
}

private class RulesProps(var course: Course, var task: Task, var onUpdate: OnTaskChange) : RProps

private class RulesState(var deadline: DateTime?) : RState

private class Rules(props: RulesProps) : RComponent<RulesProps, RulesState>(props) {

    private val flaxoClient: FlaxoClient

    init {
        flaxoClient = Container.flaxoClient
        state.deadline = props.task.deadline
    }

    override fun RBuilder.render() {
        div {
            deadlineRule(props.task) { newDeadline -> setState { this.deadline = newDeadline } }
            button(classes = "btn btn-primary") {
                attrs {
                    onClickFunction = { GlobalScope.launch { submitRulesChanges() } }
                    disabled = state.deadline == props.task.deadline
                }
                +"Update rules"
            }
        }
    }

    private suspend fun submitRulesChanges() {
        credentials?.also { credentials ->
            try {
                val updatedTask = flaxoClient.updateRules(credentials, props.course.name, props.task.branch,
                        state.deadline?.toDateString())
                props.onUpdate(updatedTask)
                Notifications.success("Task rules has been updated.")
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred while updating tasks rules.", e)
            }
        }
    }
}
