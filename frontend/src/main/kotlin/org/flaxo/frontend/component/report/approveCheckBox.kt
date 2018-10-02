package org.flaxo.frontend.component.report

import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import org.flaxo.common.Solution
import org.flaxo.common.Task
import org.flaxo.frontend.checkBoxValue
import react.RBuilder
import react.dom.input
import react.dom.label
import react.dom.span

fun RBuilder.approveCheckBox(task: Task,
                             solution: Solution,
                             onSolutionApprovalStatusChange: (String, Boolean) -> Unit
) =
        label(classes = "switch switch-small") {
            input(type = InputType.checkBox) {
                attrs {
                    id = "approvalCheckBoxId-" + task.branch + solution.student
                    checked = solution.approved
                    onChangeFunction = { onSolutionApprovalStatusChange(solution.student, checkBoxValue(id)) }
                }
            }
            span(classes = "slider") { }
        }
