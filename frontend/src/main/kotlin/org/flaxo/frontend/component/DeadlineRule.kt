package org.flaxo.frontend.component

import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.data.Task
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.a
import react.dom.defaultValue
import react.dom.div
import react.dom.input
import react.dom.small
import kotlin.browser.document
import kotlin.js.Date

fun RBuilder.deadlineRule(task: Task, onDeadlineChange: (Date?) -> Unit) =
        child(DeadlineRule::class) {
            attrs {
                this.task = task
                this.onDeadlineChange = onDeadlineChange
            }
        }

class DeadlineRuleProps(var task: Task,
                        var onDeadlineChange: (Date?) -> Unit) : RProps

class DeadlineRule(props: DeadlineRuleProps) : RComponent<DeadlineRuleProps, EmptyState>(props) {

    private companion object {
        // TODO 18.08.18: Input and help ids can be non-unique between task tabs
        val DEADLINE_RULE_INPUT_ID = "deadlineRule"
        val DEADLINE_RULE_HELP_ID = "deadlineRuleHelp"
    }

    override fun RBuilder.render() {
        div(classes = "task-deadline-rule") {
            div(classes = "form-group") {
                label("Deadline", forInput = DEADLINE_RULE_INPUT_ID)
                input(classes = "form-control", type = InputType.date) {
                    attrs {
                        id = DEADLINE_RULE_INPUT_ID
                        defaultValue = props.task.deadline?.toISOString()?.substring(0, 10) ?: ""
                        onChangeFunction = { event ->
                            val target = event.target as HTMLInputElement
                            props.onDeadlineChange(Date(target.value))
                        }
                        attributes["aria-describedby"] = DEADLINE_RULE_HELP_ID
                    }
                }
                small(classes = "form-text text-muted") {
                    attrs {
                        id = DEADLINE_RULE_HELP_ID
                    }
                    if (props.task.deadline != null) {
                        +"All solutions received after that date will be fined. "
                        +"Also you can "
                        a(href = "#") {
                            +"remove the deadline."
                            attrs {
                                onClickFunction = { event ->
                                    event.preventDefault()
                                    (document.getElementById(DEADLINE_RULE_INPUT_ID) as? HTMLInputElement)
                                            ?.apply { value = "" }
                                    props.onDeadlineChange(null)
                                }
                            }
                        }
                    } else {
                        +"All solutions received after that date will be fined."
                    }
                }
            }
        }
    }

}
