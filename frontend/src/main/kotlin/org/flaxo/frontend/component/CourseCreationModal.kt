import kotlinx.coroutines.experimental.launch
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.data.Language
import org.w3c.dom.HTMLInputElement
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.hidden
import kotlinx.html.id
import kotlinx.html.role
import kotlinx.html.tabIndex
import org.flaxo.frontend.component.label
import org.flaxo.frontend.credentials
import org.flaxo.frontend.wrapper.NotificationManager
import org.w3c.dom.HTMLSelectElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.setState
import react.dom.button
import react.dom.div
import react.dom.form
import react.dom.h5
import react.dom.input
import react.dom.option
import react.dom.select
import react.dom.small
import react.dom.span
import kotlin.browser.document

fun RBuilder.courseCreationModal(onCourseCreation: () -> Unit) = child(CourseCreationModal::class) {
    attrs {
        this.onCourseCreation = onCourseCreation
    }
}

class CourseCreationModalProps(var onCourseCreation: () -> Unit) : RProps
class CourseCreationModalState(var language: String? = null,
                               var testingLanguage: String? = null,
                               var testingFramework: String? = null,
                               var flaxoLanguages: List<Language> = emptyList()) : RState

class CourseCreationModal(props: CourseCreationModalProps)
    : RComponent<CourseCreationModalProps, CourseCreationModalState>(props) {

    companion object {
        const val COURSE_CREATION_MODAL_ID = "courseCreationModal"
        private const val COURSE_NAME_INPUT_ID = "courseNameInput"
        private const val COURSE_NAME_INPUT_HELP_ID = "courseNameInputHelp"
        private const val COURSE_DESCRIPTION_INPUT_ID = "courseDescriptionInput"
        private const val COURSE_DESCRIPTION_INPUT_HELP_ID = "courseDescriptionInputHelp"
        private const val LANGUAGE_SELECT_ID = "languageSelect"
        private const val LANGUAGE_SELECT_HELP_ID = "languageInputHelp"
        private const val TESTING_LANGUAGE_SELECT_ID = "testingLanguageSelect"
        private const val TESTING_LANGUAGE_SELECT_HELP_ID = "testingLanguageSelectHelp"
        private const val TESTING_FRAMEWORK_SELECT_ID = "testingFrameworkSelect"
        private const val TESTING_FRAMEWORK_SELECT_HELP_ID = "testingFrameworkSelectHelp"
        private const val NUMBER_OF_TASKS_INPUT_ID = "numberOfTasksInput"
        private const val NUMBER_OF_TASKS_INPUT_HELP_ID = "numberOfTasksInputHelp"
    }

    private val flaxoClient: FlaxoClient

    init {
        state = CourseCreationModalState()
        flaxoClient = Container.flaxoClient
        launch {
            flaxoClient.getAvailableLanguages().also { languages ->
                setState {
                    flaxoLanguages = languages
                    language = languages.firstOrNull()
                            ?.name
                            ?: "not found"
                    testingLanguage = languages.firstOrNull()
                            ?.compatibleTestingLanguages
                            ?.firstOrNull()
                            ?: "not found"
                    testingFramework = languages.firstOrNull()
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
                            +"Modal title"
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
                        form {
                            courseNameInput()
                            courseDescriptionInput()
                            languageSelect()
                            testingLanguageSelect()
                            testingFrameworkSelect()
                            tasksNumberInput()
                        }
                    }
                    div("modal-footer") {
                        button(classes = "btn btn-primary", type = ButtonType.button) {
                            attrs {
                                onClickFunction = { createCourse() }
                                attributes["data-dismiss"] = "modal"
                            }
                            +"Create"
                        }
                        button(classes = "btn btn-secondary", type = ButtonType.button) {
                            attrs {
                                attributes["data-dismiss"] = "modal"
                            }
                            +"Cancel"
                        }
                    }
                }
            }
        }
    }

    private fun createCourse() {
        credentials?.also {
            try {
                val courseName = valueByInputId(COURSE_NAME_INPUT_ID) ?: ""
                val description = valueByInputId(COURSE_NAME_INPUT_ID)
                val language = valueBySelectId(LANGUAGE_SELECT_ID) ?: ""
                val testingLanguage = valueBySelectId(TESTING_LANGUAGE_SELECT_ID) ?: ""
                val testingFramework = valueBySelectId(TESTING_FRAMEWORK_SELECT_ID) ?: ""
                val numberOfTasks = valueByInputId(NUMBER_OF_TASKS_INPUT_ID)?.toInt() ?: 0
                flaxoClient.createCourse(it,
                        courseName = courseName,
                        description = description,
                        language = language,
                        testingLanguage = testingLanguage,
                        testingFramework = testingFramework,
                        numberOfTasks = numberOfTasks)
                props.onCourseCreation()
                NotificationManager.success("Course has been created.")
            } catch (e: Exception) {
                console.log(e)
                NotificationManager.success("Error occurred while creation course.")
            }
        }
    }

    private fun valueBySelectId(selectId: String): String? = document.getElementById(selectId)
            ?.let { it as? HTMLSelectElement }
            ?.value

    private fun valueByInputId(inputId: String): String? = document.getElementById(inputId)
            ?.let { it as? HTMLInputElement }
            ?.value

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
                                    ?.compatibleTestingLanguages
                                    ?.firstOrNull()
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
                state.flaxoLanguages.forEach { option { +it.name } }
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
                        ?.compatibleTestingLanguages
                        ?.forEach { option { +it } }
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

}
