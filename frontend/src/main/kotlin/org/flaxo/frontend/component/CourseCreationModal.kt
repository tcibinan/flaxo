package org.flaxo.frontend.component

import kotlinx.coroutines.experimental.launch
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.common.data.Language
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.hidden
import kotlinx.html.id
import kotlinx.html.role
import kotlinx.html.tabIndex
import org.flaxo.frontend.credentials
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.checkBoxValue
import org.flaxo.frontend.clickOnButton
import org.flaxo.frontend.client.FlaxoHttpException
import org.flaxo.frontend.inputValue
import org.flaxo.frontend.selectValue
import org.flaxo.frontend.validatedInputValue
import org.w3c.dom.HTMLSelectElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.setState
import react.dom.button
import react.dom.div
import react.dom.h5
import react.dom.input
import react.dom.option
import react.dom.select
import react.dom.small
import react.dom.span

fun RBuilder.courseCreationModal(onCourseCreation: () -> Unit) = child(CourseCreationModal::class) {
    attrs {
        this.onCourseCreation = onCourseCreation
    }
}

class CourseCreationModalProps(var onCourseCreation: () -> Unit) : RProps
class CourseCreationModalState(var generateEnvironment: Boolean = false,
                               var language: String? = null,
                               var testingLanguage: String? = null,
                               var testingFramework: String? = null,
                               var flaxoLanguages: List<Language> = emptyList()
) : RState

class CourseCreationModal(props: CourseCreationModalProps)
    : RComponent<CourseCreationModalProps, CourseCreationModalState>(props) {

    companion object {
        const val COURSE_CREATION_MODAL_ID = "courseCreationModal"
        private const val COURSE_NAME_INPUT_ID = "courseNameInput"
        private const val COURSE_NAME_INPUT_HELP_ID = "courseNameInputHelp"
        private const val COURSE_DESCRIPTION_INPUT_ID = "courseDescriptionInput"
        private const val COURSE_DESCRIPTION_INPUT_HELP_ID = "courseDescriptionInputHelp"
        private const val GENERATE_ENVIRONMENT_CHECKBOX_ID = "generateEnvironmentCheckbox"
        private const val GENERATE_ENVIRONMENT_CHECKBOX_HELP_ID = "generateEnvironmentCheckboxHelp"
        private const val LANGUAGE_SELECT_ID = "languageSelect"
        private const val LANGUAGE_SELECT_HELP_ID = "languageInputHelp"
        private const val TESTING_LANGUAGE_SELECT_ID = "testingLanguageSelect"
        private const val TESTING_LANGUAGE_SELECT_HELP_ID = "testingLanguageSelectHelp"
        private const val TESTING_FRAMEWORK_SELECT_ID = "testingFrameworkSelect"
        private const val TESTING_FRAMEWORK_SELECT_HELP_ID = "testingFrameworkSelectHelp"
        private const val NUMBER_OF_TASKS_INPUT_ID = "numberOfTasksInput"
        private const val NUMBER_OF_TASKS_INPUT_HELP_ID = "numberOfTasksInputHelp"
        private const val COURSE_CREATION_MODAL_CANCEL_ID = "courseCreationModalCancel"
    }

    private val flaxoClient: FlaxoClient

    init {
        state = CourseCreationModalState()
        flaxoClient = Container.flaxoClient
        launch {
            flaxoClient.getAvailableLanguages().also { languages ->
                setState {
                    val defaultLanguage = languages.firstOrNull()
                    val defaultTestingLanguage = defaultLanguage?.compatibleTestingLanguages?.firstOrNull()

                    flaxoLanguages = languages
                    language = defaultLanguage?.name ?: "not found"
                    testingLanguage = defaultTestingLanguage ?: "not found"
                    testingFramework = defaultTestingLanguage
                            ?.let { testingLanguageName -> languages.find { it.name == testingLanguageName } }
                            ?.compatibleTestingFrameworks
                            ?.firstOrNull()
                            ?: "not found"
                }
            }
        }
    }

    override fun RBuilder.render() {
        div("modal fade") {
            attrs {
                id = COURSE_CREATION_MODAL_ID
                tabIndex = "-1"
                role = "dialog"
                attributes["aria-hidden"] = "true"
            }
            div("modal-dialog") {
                attrs {
                    role = "document"
                }
                div("modal-content") {
                    div("modal-header") {
                        h5("modal-title") {
                            +"Create course"
                        }
                        button(classes = "close", type = ButtonType.button) {
                            attrs {
                                attributes["data-dismiss"] = "modal"
                                attributes["aria-label"] = "Close"
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
                        tasksNumberInput()
                        generateEnvironmentCheckbox()
                        if (state.generateEnvironment) {
                            languageSelect()
                            testingLanguageSelect()
                            testingFrameworkSelect()
                        }
                    }
                    div("modal-footer") {
                        button(classes = "btn btn-primary", type = ButtonType.button) {
                            attrs {
                                onClickFunction = { launch { createCourse() } }
                            }
                            +"Create"
                        }
                        button(classes = "btn btn-secondary", type = ButtonType.button) {
                            attrs {
                                id = COURSE_CREATION_MODAL_CANCEL_ID
                                attributes["data-dismiss"] = "modal"
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
                    attributes["aria-describedby"] = COURSE_NAME_INPUT_HELP_ID
                }
            }
            small {
                attrs {
                    id = COURSE_NAME_INPUT_HELP_ID
                    classes = setOf("form-text", "text-muted")
                }
                +"Course name should be a valid git repository name"
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
                    attributes["aria-describedby"] = COURSE_DESCRIPTION_INPUT_HELP_ID
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

    private fun RBuilder.generateEnvironmentCheckbox() {
        div("form-check") {
            input {
                attrs {
                    id = GENERATE_ENVIRONMENT_CHECKBOX_ID
                    classes = setOf("form-check-input")
                    type = InputType.checkBox
                    attributes["aria-describedby"] = GENERATE_ENVIRONMENT_CHECKBOX_HELP_ID
                    defaultChecked = state.generateEnvironment
                    onClickFunction = { setState { generateEnvironment = !generateEnvironment } }
                }
            }
            label("Enable environment generation (experimental)", GENERATE_ENVIRONMENT_CHECKBOX_ID)
        }
    }

    private fun RBuilder.languageSelect() {
        div("form-group") {
            label("Language", LANGUAGE_SELECT_ID)
            select {
                attrs {
                    id = LANGUAGE_SELECT_ID
                    classes = setOf("form-control")
                    onChangeFunction = { event ->
                        val target = event.target as HTMLSelectElement
                        setState {
                            language = target.value
                            testingLanguage = state.flaxoLanguages
                                    .find { it.name == language }
                                    ?.compatibleTestingLanguages.orEmpty()
                                    .mapNotNull { testingLanguageName ->
                                        state.flaxoLanguages.find { it.name == testingLanguageName }
                                    }
                                    .firstOrNull { it.compatibleTestingFrameworks.isNotEmpty() }
                                    ?.name
                                    ?: "not found"
                            testingFramework = state.flaxoLanguages
                                    .find { it.name == testingLanguage }
                                    ?.compatibleTestingFrameworks
                                    ?.firstOrNull()
                                    ?: "not found"
                        }
                    }
                    attributes["defaultValue"] = state.language ?: ""
                    attributes["aria-describedby"] = LANGUAGE_SELECT_HELP_ID
                }
                state.flaxoLanguages
                        .filter { it.compatibleTestingLanguages.isNotEmpty() }
                        .forEach { option { +it.name } }
            }
            small {
                attrs {
                    id = LANGUAGE_SELECT_HELP_ID
                    classes = setOf("form-text", "text-muted")
                }
                +"Language solutions will be written on"
            }
        }
    }

    private fun RBuilder.testingLanguageSelect() {
        div("form-group") {
            label("Testing language", TESTING_LANGUAGE_SELECT_ID)
            select {
                attrs {
                    id = TESTING_LANGUAGE_SELECT_ID
                    classes = setOf("form-control")
                    onChangeFunction = { event ->
                        val target = event.target as HTMLSelectElement
                        setState {
                            testingLanguage = target.value
                            testingFramework = state.flaxoLanguages
                                    .find { it.name == testingLanguage }
                                    ?.compatibleTestingFrameworks
                                    ?.firstOrNull()
                                    ?: "not found"
                        }
                    }
                    attributes["defaultValue"] = state.testingLanguage ?: ""
                    attributes["aria-describedby"] = TESTING_LANGUAGE_SELECT_HELP_ID
                }
                state.flaxoLanguages
                        .find { it.name == state.language }
                        ?.compatibleTestingLanguages.orEmpty()
                        .mapNotNull { testingLanguageName -> state.flaxoLanguages.find { it.name == testingLanguageName } }
                        .map { it.name }
                        .forEach { option { +it } }
            }
            small {
                attrs {
                    id = TESTING_LANGUAGE_SELECT_HELP_ID
                    classes = setOf("form-text", "text-muted")
                }
                +"Language tests will be written on"
            }
        }
    }

    private fun RBuilder.testingFrameworkSelect() {
        div("form-group") {
            label("Testing framework", TESTING_FRAMEWORK_SELECT_ID)
            select {
                attrs {
                    id = TESTING_FRAMEWORK_SELECT_ID
                    classes = setOf("form-control")
                    attributes["defaultValue"] = state.testingFramework ?: ""
                    attributes["aria-describedby"] = TESTING_FRAMEWORK_SELECT_HELP_ID
                }

                state.flaxoLanguages
                        .find { it.name == state.testingLanguage }
                        ?.compatibleTestingFrameworks
                        ?.forEach { option { +it } }
            }
            small {
                attrs {
                    id = TESTING_FRAMEWORK_SELECT_HELP_ID
                    classes = setOf("form-text", "text-muted")
                }
                +"Test framework to use in course"
            }
        }
    }

    private fun RBuilder.tasksNumberInput() {
        div("form-group") {
            label("Number of tasks", NUMBER_OF_TASKS_INPUT_ID)
            input {
                attrs {
                    id = NUMBER_OF_TASKS_INPUT_ID
                    classes = setOf("form-control")
                    type = InputType.number
                    attributes["aria-describedby"] = NUMBER_OF_TASKS_INPUT_HELP_ID
                }
            }
            small {
                attrs {
                    id = NUMBER_OF_TASKS_INPUT_HELP_ID
                    classes = setOf("form-text", "text-muted")
                }
                +"Number of tasks in course. Each branch represents a single task"
            }
        }
    }

    private suspend fun createCourse() {
        credentials?.also {
            try {
                val courseName = validatedInputValue(COURSE_NAME_INPUT_ID)
                val description = inputValue(COURSE_NAME_INPUT_ID)
                val generateEnvironment = checkBoxValue(GENERATE_ENVIRONMENT_CHECKBOX_ID)
                val numberOfTasks = validatedInputValue(NUMBER_OF_TASKS_INPUT_ID)?.toInt()
                val language = selectValue(LANGUAGE_SELECT_ID)
                val testingLanguage = selectValue(TESTING_LANGUAGE_SELECT_ID)
                val testingFramework = selectValue(TESTING_FRAMEWORK_SELECT_ID)
                if (courseName != null && numberOfTasks != null) {
                    clickOnButton(COURSE_CREATION_MODAL_CANCEL_ID)
                    Notifications.info("Course creation has been started.")
                    flaxoClient.createCourse(it,
                            courseName = courseName,
                            description = description,
                            language = language?.filter { generateEnvironment },
                            testingLanguage = testingLanguage?.filter { generateEnvironment },
                            testingFramework = testingFramework?.filter { generateEnvironment },
                            numberOfTasks = numberOfTasks)
                    props.onCourseCreation()
                    Notifications.success("Course has been created.")
                }
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred while course creation.", e)
            }
        }
    }

}
