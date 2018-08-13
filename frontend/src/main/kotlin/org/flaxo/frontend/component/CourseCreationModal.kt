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
import org.flaxo.frontend.data.CourseParameters
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

fun RBuilder.courseCreationModal(onCourseCreation: () -> Unit) = child(CourseCreationModal::class) {
    attrs {
        this.onCourseCreation = onCourseCreation
    }
}

class CourseCreationModalProps(var onCourseCreation: () -> Unit) : RProps
class CourseCreationModalState(var courseName: String? = null,
                               var courseDescription: String? = null,
                               var language: String? = null,
                               var testingLanguage: String? = null,
                               var testingFramework: String? = null,
                               var numberOfTasks: Int? = null,
                               var flaxoLanguages: List<Language> = emptyList()) : RState

class CourseCreationModal(props: CourseCreationModalProps)
    : RComponent<CourseCreationModalProps, CourseCreationModalState>(props) {

    private companion object {
        const val COURSE_CREATION_MODAL_ID = "courseCreationModal"
        const val COURSE_NAME_INPUT_ID = "courseNameInput"
        const val COURSE_NAME_INPUT_HELP_ID = "courseNameInputHelp"
        const val COURSE_DESCRIPTION_INPUT_ID = "courseDescriptionInput"
        const val COURSE_DESCRIPTION_INPUT_HELP_ID = "courseDescriptionInputHelp"
        const val LANGUAGE_SELECT_ID = "languageSelect"
        const val LANGUAGE_SELECT_HELP_ID = "languageInputHelp"
        const val TESTING_LANGUAGE_SELECT_ID = "testingLanguageSelect"
        const val TESTING_LANGUAGE_SELECT_HELP_ID = "testingLanguageSelectHelp"
        const val TESTING_FRAMEWORK_SELECT_ID = "testingFrameworkSelect"
        const val TESTING_FRAMEWORK_SELECT_HELP_ID = "testingFrameworkSelectHelp"
        const val NUMBER_OF_TASKS_INPUT_ID = "numberOfTasksInput"
        const val NUMBER_OF_TASKS_INPUT_HELP_ID = "numberOfTasksInputHelp"
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
        button(classes = "btn btn-outline-primary", type = ButtonType.button) {
            attrs {
                attributes["data-toggle"] = "modal"
                attributes["data-target"] = "#$COURSE_CREATION_MODAL_ID"
            }
            +"Create course"
        }
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
                // TODO 12.08.18: Create actual course parameters object
                flaxoClient.createCourse(it, CourseParameters())
                props.onCourseCreation()
                // TODO 12.08.18: Notify user that course creation has finished successfully
            } catch (e: Throwable) {
                // TODO 12.08.18: Notify user that course creation has failed
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
                    onChangeFunction = { event ->
                        val target = event.target as HTMLInputElement
                        setState { courseName = target.value }
                    }
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
                    onChangeFunction = { event ->
                        val target = event.target as HTMLInputElement
                        setState { courseDescription = target.value }
                    }
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
                    attributes["aria-describedby"] = LANGUAGE_SELECT_HELP_ID
                }
                state.flaxoLanguages.forEach {
                    option {
                        attrs.selected = it.name == state.language
                        +it.name
                    }
                }
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
                    attributes["aria-describedby"] = TESTING_LANGUAGE_SELECT_HELP_ID
                }

                state.flaxoLanguages
                        .find { it.name == state.language }
                        ?.compatibleTestingLanguages
                        ?.forEach {
                            option {
                                attrs.selected = it == state.testingLanguage
                                +it
                            }
                        }
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
                    onChangeFunction = { event ->
                        val target = event.target as HTMLSelectElement
                        setState { testingFramework = target.value }
                    }
                    attributes["aria-describedby"] = TESTING_FRAMEWORK_SELECT_HELP_ID
                }

                state.flaxoLanguages
                        .find { it.name == state.testingLanguage }
                        ?.compatibleTestingFrameworks
                        ?.forEach {
                            option {
                                attrs.selected = it == state.testingFramework
                                +it
                            }
                        }
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
                    onChangeFunction = { event ->
                        val target = event.target as HTMLInputElement
                        setState { numberOfTasks = target.value.toInt() }
                    }
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
