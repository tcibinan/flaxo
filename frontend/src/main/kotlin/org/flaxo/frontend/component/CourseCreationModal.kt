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
import org.flaxo.common.data.Language
import org.flaxo.frontend.Container
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.checkBoxValue
import org.flaxo.frontend.clickOnButton
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpException
import org.flaxo.frontend.component.bootstrap.selectComponent
import org.flaxo.frontend.credentials
import org.flaxo.frontend.inputValue
import org.flaxo.frontend.selectValue
import org.flaxo.frontend.validatedInputValue
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.b
import react.dom.button
import react.dom.div
import react.dom.h5
import react.dom.input
import react.dom.small
import react.dom.span
import react.setState

const val COURSE_CREATION_MODAL_ID = "courseCreationModal"
private const val COURSE_NAME_INPUT_ID = "courseNameInput"
private const val COURSE_NAME_INPUT_HELP_ID = "courseNameInputHelp"
private const val COURSE_DESCRIPTION_INPUT_ID = "courseDescriptionInput"
private const val COURSE_DESCRIPTION_INPUT_HELP_ID = "courseDescriptionInputHelp"
private const val GENERATE_ENVIRONMENT_CHECKBOX_ID = "generateEnvironmentCheckbox"
private const val GENERATE_ENVIRONMENT_CHECKBOX_HELP_ID = "generateEnvironmentCheckboxHelp"
private const val LANGUAGE_SELECT_ID = "languageSelect"
private const val TESTING_LANGUAGE_SELECT_ID = "testingLanguageSelect"
private const val TESTING_FRAMEWORK_SELECT_ID = "testingFrameworkSelect"
private const val NUMBER_OF_TASKS_INPUT_ID = "numberOfTasksInput"
private const val NUMBER_OF_TASKS_INPUT_HELP_ID = "numberOfTasksInputHelp"
private const val COURSE_CREATION_MODAL_CANCEL_ID = "courseCreationModalCancel"

/**
 * Adds course creation modal.
 */
fun RBuilder.courseCreationModal(onCourseCreation: () -> Unit) = child(CourseCreationModal::class) {
    attrs {
        this.onCourseCreation = onCourseCreation
    }
}

private class CourseCreationModalProps(var onCourseCreation: () -> Unit) : RProps

private class CourseCreationModalState(var generateEnvironment: Boolean = false,
                                       var language: String? = null,
                                       var testingLanguage: String? = null,
                                       var testingFramework: String? = null,
                                       var flaxoLanguages: List<Language> = emptyList()
) : RState

private class CourseCreationModal(props: CourseCreationModalProps)
    : RComponent<CourseCreationModalProps, CourseCreationModalState>(props) {

    private val flaxoClient: FlaxoClient

    init {
        state = CourseCreationModalState()
        flaxoClient = Container.flaxoClient
        GlobalScope.launch {
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
                ariaHidden = true
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
                        tasksNumberInput()
                        languageSelect()
                        generateEnvironmentCheckbox()
                        if (state.generateEnvironment) {
                            testingLanguageSelect()
                            testingFrameworkSelect()
                        }
                    }
                    div("modal-footer") {
                        button(classes = "btn btn-primary", type = ButtonType.button) {
                            attrs {
                                onClickFunction = { GlobalScope.launch { createCourse() } }
                            }
                            +"Create"
                        }
                        button(classes = "btn btn-secondary", type = ButtonType.button) {
                            attrs {
                                id = COURSE_CREATION_MODAL_CANCEL_ID
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

    private fun RBuilder.generateEnvironmentCheckbox() {
        val isDisabled = state.flaxoLanguages
                .find { it.name == state.language }
                ?.compatibleTestingLanguages
                .isNullOrEmpty()
        div("form-group") {
            div("form-check") {
                input {
                    attrs {
                        id = GENERATE_ENVIRONMENT_CHECKBOX_ID
                        classes = setOf("form-check-input")
                        type = InputType.checkBox
                        ariaDescribedBy = GENERATE_ENVIRONMENT_CHECKBOX_HELP_ID
                        defaultChecked = state.generateEnvironment
                        disabled = isDisabled
                        onClickFunction = { setState { generateEnvironment = !generateEnvironment } }
                    }
                }
                val checkboxLabels =
                        if (isDisabled) setOf("checkbox-label", "text-muted")
                        else setOf("checkbox-label")
                label("Generate environment", GENERATE_ENVIRONMENT_CHECKBOX_ID, classes = checkboxLabels)
            }
            small {
                attrs {
                    id = GENERATE_ENVIRONMENT_CHECKBOX_HELP_ID
                    classes = setOf("form-text", "text-muted", "checkbox-help")
                }
                +"Environment generation is an "
                b { +"experiment feature " }
                +"that is available only for several languages and frameworks."
            }
        }
    }

    private fun RBuilder.languageSelect(): Unit = selectComponent(
            selectId = LANGUAGE_SELECT_ID,
            name = "Language",
            description = "Programming language that will be used by the course students in their solutions. "
                    + "It should be specified in order to perform plagiarism analysis.",
            default = state.language,
            options = state.flaxoLanguages
                    .filter { it.compatibleTestingLanguages.isNotEmpty() || !state.generateEnvironment }
                    .map { it.name },
            onUpdate = {
                setState {
                    language = it
                    testingLanguage = state.flaxoLanguages
                            .find { it.name == language }
                            ?.compatibleTestingLanguages.orEmpty()
                            .mapNotNull { testingLanguageName ->
                                state.flaxoLanguages.find { it.name == testingLanguageName }
                            }
                            .firstOrNull { it.compatibleTestingFrameworks.isNotEmpty() }
                            ?.name
                    testingFramework = state.flaxoLanguages
                            .find { it.name == testingLanguage }
                            ?.compatibleTestingFrameworks
                            ?.firstOrNull()
                }
            }
    )

    private fun RBuilder.testingLanguageSelect(): Unit = selectComponent(
            selectId = TESTING_LANGUAGE_SELECT_ID,
            name = "Testing language",
            description = "Programming language that will be used by the course author in task specifications. "
                    + "It should be specified in order to autobuild project infrastructure.",
            default = state.testingLanguage,
            options = state.flaxoLanguages
                    .find { it.name == state.language }
                    ?.compatibleTestingLanguages.orEmpty()
                    .mapNotNull { testingLanguageName -> state.flaxoLanguages.find { it.name == testingLanguageName } }
                    .map { it.name },
            onUpdate = {
                setState {
                    testingLanguage = it
                    testingFramework = state.flaxoLanguages
                            .find { it.name == testingLanguage }
                            ?.compatibleTestingFrameworks
                            ?.firstOrNull()
                }
            }
    )

    private fun RBuilder.testingFrameworkSelect() = selectComponent(
            selectId = TESTING_FRAMEWORK_SELECT_ID,
            name = "Testing framework",
            description = "Testing framework that will be used with the corresponding testing language " +
                    "by the course author in task specifications. It should be specified in order to autobuild an " +
                    "associated repository infrastructure.",
            default = state.testingFramework,
            options = state.flaxoLanguages
                    .find { it.name == state.testingLanguage }
                    ?.compatibleTestingFrameworks
                    ?: emptyList()
    )

    private fun RBuilder.tasksNumberInput() {
        div("form-group") {
            label("Number of tasks", NUMBER_OF_TASKS_INPUT_ID)
            input {
                attrs {
                    id = NUMBER_OF_TASKS_INPUT_ID
                    classes = setOf("form-control")
                    type = InputType.number
                    ariaDescribedBy = NUMBER_OF_TASKS_INPUT_HELP_ID
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
                            language = language,
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
