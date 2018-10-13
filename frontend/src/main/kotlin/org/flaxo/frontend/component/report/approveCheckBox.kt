package org.flaxo.frontend.component.report

import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.hidden
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import kotlinx.html.tabIndex
import org.flaxo.common.Solution
import org.flaxo.common.SolutionReview
import org.flaxo.common.Task
import org.flaxo.frontend.checkBoxValue
import org.flaxo.frontend.clickOnButton
import org.flaxo.frontend.component.ariaHidden
import org.flaxo.frontend.component.ariaLabel
import org.flaxo.frontend.component.ariaLabelledBy
import org.flaxo.frontend.component.dataDismiss
import org.flaxo.frontend.component.dataTarget
import org.flaxo.frontend.component.dataToggle
import org.flaxo.frontend.toggleCheckbox
import org.flaxo.frontend.validatedInputValue
import org.w3c.dom.HTMLButtonElement
import react.RBuilder
import react.dom.b
import react.dom.button
import react.dom.defaultValue
import react.dom.div
import react.dom.h5
import react.dom.input
import react.dom.label
import react.dom.span
import kotlin.browser.document

fun RBuilder.approveCheckBox(task: Task,
                             solution: Solution,
                             onReviewAddition: (String, SolutionReview) -> Unit
) {
    val approveCheckBoxId = "review-checkbox-" + task.branch + solution.student
    val approveModalId = "review-body-addition-${task.branch}-${solution.student}"
    val approveModalToggleId = "$approveModalId-toggle"
    val approveModalLabelId = "$approveModalId-label"
    val approveModalInputId = "$approveModalId-input"

    label(classes = "switch switch-small") {
        input(type = InputType.checkBox) {
            attrs {
                id = approveCheckBoxId
                defaultChecked = solution.approved
                onChangeFunction = {
                    val checkBoxValue = checkBoxValue(id)
                    if (!checkBoxValue) {
                        val modalToggle = document.getElementById(approveModalToggleId) as? HTMLButtonElement
                        modalToggle?.click()
                    } else {
                        onReviewAddition(solution.student, SolutionReview(body = null, approved = checkBoxValue))
                    }
                }
                disabled = solution.commits.isEmpty()
            }
        }
        span(classes = "slider") { }
    }

    button(classes = "btn btn-primary review-toggle-btn") {
        attrs {
            id = approveModalToggleId
            dataToggle = "modal"
            dataTarget = "#$approveModalId"
        }
    }

    div(classes = "modal fade review-modal") {
        attrs {
            id = approveModalId
            tabIndex = "-1"
            role = "dialog"
            ariaLabelledBy = approveModalLabelId
            ariaHidden = true
        }
        div(classes = "modal-dialog") {
            attrs.role = "document"
            div(classes = "modal-content") {
                div(classes = "modal-header") {
                    h5("modal-title") {
                        attrs.id = approveModalLabelId
                        +"Add review message"
                    }
                    button(classes = "close", type = ButtonType.button) {
                        attrs {
                            dataDismiss = "modal"
                            ariaLabel = "close"
                        }
                        span {
                            attrs.hidden = true
                            +"&times;"
                        }
                    }
                }
                div(classes = "modal-body") {
                    div(classes = "form-group") {
                        label {
                            attrs.attributes["htmlFor"] = approveModalInputId
                            +"Review message for "
                            b { +solution.student }
                            +" solution of "
                            b { +solution.task }
                            +" task"
                        }
                        input(classes = "form-control") {
                            attrs {
                                id = approveModalInputId
                                defaultValue = "Changes requested!"
                            }
                        }
                    }
                }
                div(classes = "modal-footer") {
                    button(classes = "btn btn-secondary", type = ButtonType.button) {
                        attrs {
                            dataDismiss = "modal"
                            onClickFunction = { toggleCheckbox(approveCheckBoxId) }
                        }
                        +"Cancel"
                    }
                    button(classes = "btn btn-primary", type = ButtonType.button) {
                        +"Accept"
                        attrs {
                            onClickFunction = {
                                val reviewBody = validatedInputValue(approveModalInputId)

                                if (reviewBody != null) {
                                    onReviewAddition(solution.student,
                                            SolutionReview(body = reviewBody, approved = false)
                                    )
                                    clickOnButton(approveModalToggleId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

