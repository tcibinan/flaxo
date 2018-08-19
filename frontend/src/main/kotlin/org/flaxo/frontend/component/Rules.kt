package org.flaxo.frontend.component

import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.credentials
import org.flaxo.frontend.data.Course
import org.flaxo.frontend.data.Task
import org.flaxo.frontend.wrapper.NotificationManager
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.button
import react.dom.div
import react.setState
import kotlin.js.Date


fun RBuilder.rules(course: Course, task: Task) = child(Rules::class) {
    attrs {
        this.course = course
        this.task = task
    }
}

class RulesProps(var course: Course,
                 var task: Task) : RProps

class RulesState(var deadline: Date?) : RState

class Rules(props: RulesProps) : RComponent<RulesProps, RulesState>(props) {

    private val flaxoClient: FlaxoClient

    init {
        flaxoClient = Container.flaxoClient
        state.deadline = props.task.deadline
    }

    override fun RBuilder.render() {
        div(classes = "rules-list") {
            deadlineRule(props.task) { newDeadline -> setState { this.deadline = newDeadline } }
            button(classes = "btn btn-primary") {
                attrs {
                    onClickFunction = { submitRulesChanges() }
                    disabled = state.deadline == props.task.deadline
                }
                +"Update rules"
            }
        }
    }

    private fun submitRulesChanges() {
        credentials?.also { credentials ->
            try {
                flaxoClient.updateRules(credentials, props.course.name, props.task.branch,
                        state.deadline?.toISOString()?.substring(0, 10))
                NotificationManager.success("Task rules has been updated.")
            } catch (e: Exception) {
                console.log(e)
                NotificationManager.error("Error occurred while updating tasks rules.")
            }
        }
    }

}
