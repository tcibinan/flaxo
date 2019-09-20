package org.flaxo.frontend.component

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.hidden
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import kotlinx.html.tabIndex
import org.flaxo.frontend.Container
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.OnCourseChange
import org.flaxo.frontend.clickOnButton
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpException
import org.flaxo.frontend.credentials
import org.flaxo.frontend.inputValue
import org.flaxo.frontend.validatedInputValue
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.button
import react.dom.div
import react.dom.h5
import react.dom.input
import react.dom.small
import react.dom.span

const val COURSE_IMPORT_MODAL_ID = "courseImportModal"
private const val COURSE_NAME_INPUT_ID = "courseImportModalNameInput"
private const val COURSE_NAME_INPUT_HELP_ID = "courseImportModalNameInputHelp"
private const val COURSE_DESCRIPTION_INPUT_ID = "courseImportModalDescriptionInput"
private const val COURSE_DESCRIPTION_INPUT_HELP_ID = "courseImportModalDescriptionInputHelp"
private const val COURSE_IMPORT_MODAL_CANCEL_ID = "courseImportModalCancel"

/**
 * Adds course import modal.
 */
fun RBuilder.courseImportModal(onImport: OnCourseChange) = child(CourseImportModal::class) {
    attrs {
        this.onImport = onImport
    }
}

private class CourseImportModalProps(var onImport: OnCourseChange) : RProps

private class CourseImportModalState(var name: String? = null
) : RState

private class CourseImportModal(props: CourseImportModalProps)
    : RComponent<CourseImportModalProps, CourseImportModalState>(props) {

    private val flaxoClient: FlaxoClient

    init {
        state = CourseImportModalState()
        flaxoClient = Container.flaxoClient
    }

    override fun RBuilder.render() {
        div("modal fade") {
            attrs {
                id = COURSE_IMPORT_MODAL_ID
                tabIndex = "-1"
                role = "dialog"
                ariaHidden = true
            }
            div("modal-dialog") {
                attrs {
                    role = "document"
                }
                div("modal-content") {
                    div("modal-header") {
                        h5("modal-title") {
                            +"Import course"
                        }
                        button(classes = "close", type = ButtonType.button) {
                            attrs {
                                dataDismiss = "modal"
                                ariaLabel = "Close"
                            }
                            span {
                                attrs {
                                    hidden = true
                                }
                                +"&times;"
                            }
                        }
                    }
                    div("modal-body") {
                        courseNameInput()
                        courseDescriptionInput()
                    }
                    div("modal-footer") {
                        button(classes = "btn btn-primary", type = ButtonType.button) {
                            attrs {
                                onClickFunction = { GlobalScope.launch { importCourse() } }
                            }
                            +"Import"
                        }
                        button(classes = "btn btn-secondary", type = ButtonType.button) {
                            attrs {
                                id = COURSE_IMPORT_MODAL_CANCEL_ID
                                dataDismiss = "modal"
                            }
                            +"Cancel"
                        }
                    }
                }
            }
        }
    }

    private fun RBuilder.courseNameInput() {
        div("form-group") {
            label("Course name", COURSE_NAME_INPUT_ID)
            input {
                attrs {
                    id = COURSE_NAME_INPUT_ID
                    classes = setOf("form-control")
                    type = InputType.text
                    ariaDescribedBy = COURSE_NAME_INPUT_HELP_ID
                }
            }
            small {
                attrs {
                    id = COURSE_NAME_INPUT_HELP_ID
                    classes = setOf("form-text", "text-muted")
                }
                +"Name of an existing repository of the authenticated GitHub account"
            }
        }
    }

    private fun RBuilder.courseDescriptionInput() {
        div("form-group") {
            label("Course description", COURSE_DESCRIPTION_INPUT_ID)
            input {
                attrs {
                    id = COURSE_DESCRIPTION_INPUT_ID
                    classes = setOf("form-control")
                    type = InputType.text
                    ariaDescribedBy = COURSE_DESCRIPTION_INPUT_HELP_ID
                }
            }
            small {
                attrs {
                    id = COURSE_DESCRIPTION_INPUT_HELP_ID
                    classes = setOf("form-text", "text-muted")
                }
                +"Course description won't be visible for students"
            }
        }
    }

    private suspend fun importCourse() {
        credentials?.also {
            try {
                val courseName = validatedInputValue(COURSE_NAME_INPUT_ID)
                val description = inputValue(COURSE_DESCRIPTION_INPUT_ID)
                if (courseName != null) {
                    clickOnButton(COURSE_IMPORT_MODAL_CANCEL_ID)
                    Notifications.info("Course importing has started.")
                    val importingCourse = flaxoClient.importCourse(it,
                            courseName = courseName,
                            description = description)
                    props.onImport(importingCourse)
                    Notifications.success("Course has been imported.")
                }
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred while course importing.", e)
            }
        }
    }
}
