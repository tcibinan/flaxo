package org.flaxo.frontend.component

import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.flaxo.common.data.DateTime
import org.flaxo.common.data.Task
import org.flaxo.frontend.clearInputValue
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.a
import react.dom.defaultValue
import react.dom.div
import react.dom.input
import react.dom.small

/**
 * Adds deadline configurations.
 */
fun RBuilder.deadlineRule(task: Task, onDeadlineChange: (DateTime?) -> Unit) =
        child(DeadlineRule::class) {
            attrs {
                this.task = task
                this.onDeadlineChange = onDeadlineChange
            }
        }

private class DeadlineRuleProps(var task: Task, var onDeadlineChange: (DateTime?) -> Unit) : RProps

private class DeadlineRule(props: DeadlineRuleProps) : RComponent<DeadlineRuleProps, EmptyState>(props) {

    private val deadlineRuleInputId = "deadlineRule-" + props.task.branch
    private val deadlineRuleHelpId = "deadlineRuleHelp-" + props.task.branch

    override fun RBuilder.render() {
        div {
            div(classes = "form-group") {
                label("Deadline", forInput = deadlineRuleInputId)
                input(classes = "form-control", type = InputType.date) {
                    attrs {
                        id = deadlineRuleInputId
                        defaultValue = props.task.deadline?.toDateString() ?: ""
                        onChangeFunction = { event ->
                            val target = event.target as HTMLInputElement
                            props.onDeadlineChange(DateTime.fromDateTimeString(target.value))
                        }
                        attributes["aria-describedby"] = deadlineRuleHelpId
                    }
                }
                small(classes = "form-text text-muted") {
                    attrs.id = deadlineRuleHelpId
                    if (props.task.deadline != null) {
                        +"All solutions that will be received after the deadline will be fined. "
                        +"Otherwise you can "
                        a(href = "#") {
                            +"remove the deadline."
                            attrs {
                                onClickFunction = { event ->
                                    event.preventDefault()
                                    clearInputValue(deadlineRuleInputId)
                                    props.onDeadlineChange(null)
                                }
                            }
                        }
                    } else {
                        +"You can specify a soft deadline for the task. "
                        +"All solutions that will be received after the deadline will be fined."
                    }
                }
            }
        }
    }

}
