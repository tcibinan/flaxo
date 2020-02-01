package org.flaxo.frontend.component

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import org.flaxo.common.Framework
import org.flaxo.common.Language
import org.flaxo.common.data.Course
import org.flaxo.common.data.CourseSettings
import org.flaxo.common.data.PlagiarismBackend
import org.flaxo.frontend.Container
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.OnCourseChange
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpException
import org.flaxo.frontend.component.bootstrap.flagComponent
import org.flaxo.frontend.component.bootstrap.inputComponent
import org.flaxo.frontend.component.bootstrap.selectComponent
import org.flaxo.frontend.credentials
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.button
import react.dom.div
import react.setState

/**
 * Adds task rules menu.
 */
fun RBuilder.courseSettings(course: Course, onUpdate: OnCourseChange) = child(CourseSettingsComponent::class) {
    attrs {
        this.course = course
        this.onUpdate = onUpdate
    }
}

private class CourseSettingsProps(var course: Course, var onUpdate: OnCourseChange) : RProps

private class CourseSettingsState(var settings: CourseSettings) : RState

private class CourseSettingsComponent(props: CourseSettingsProps)
    : RComponent<CourseSettingsProps, CourseSettingsState>(props) {

    private val flaxoClient: FlaxoClient
    private val courseLanguageSetting = "courseLanguageSetting-" + props.course.id
    private val courseTestingLanguageSetting = "courseTestingLanguageSetting-" + props.course.id
    private val courseTestingFrameworkSetting = "courseTestingFrameworkSetting-" + props.course.id
    private val coursePlagiarismAnalysisFilePatternSetting =
            "coursePlagiarismAnalysisFilePatternSetting-"+ props.course.id
    private val coursePlagiarismAnalysisBackendSetting = "coursePlagiarismAnalysisBackendSetting-" + props.course.id
    private val scoreChangeNotificationTemplateSetting = "scoreMessageTemplateSetting-" + props.course.id

    init {
        flaxoClient = Container.flaxoClient
        state.settings = props.course.settings
    }

    override fun RBuilder.render() {
        div {
            div {
                languageSelect()
                testingLanguageSelect()
                testingFrameworkSelect()
                plagiarismAnalysisFilePatternInput()
                plagiarismAnalysisBackendSelect()
                scoreChangeNotificationFlag()
                scoreChangeNotificationTemplateInput()
            }
            button(classes = "btn btn-primary") {
                attrs {
                    onClickFunction = { GlobalScope.launch { submitSettingsChanges() } }
                    disabled = state.settings == props.course.settings
                }
                +"Update settings"
            }
        }
    }

    private fun RBuilder.languageSelect() {
        selectComponent(selectId = courseLanguageSetting,
                name = "Language",
                description = "Programming language that is used by the course students in their solutions. " +
                        "It should be specified in order to perform plagiarism analysis.",
                default = state.settings.language,
                options = Language.values().map { it.alias },
                emptyOption = true,
                onUpdate = { setState { settings = settings.copy(language = it.ifBlank { null }) } }
        )
    }

    private fun RBuilder.testingLanguageSelect() {
        selectComponent(selectId = courseTestingLanguageSetting,
                name = "Testing language",
                description = "Programming language that is used by the course author in task specifications. " +
                        "It doesn't have any effect now.",
                default = state.settings.testingLanguage,
                options = Language.values().map { it.alias },
                emptyOption = true,
                onUpdate = { setState { settings = settings.copy(testingLanguage = it.ifBlank { null }) } }
        )
    }

    private fun RBuilder.testingFrameworkSelect() {
        selectComponent(selectId = courseTestingFrameworkSetting,
                name = "Testing framework",
                description = "Testing framework that is used with the corresponding testing language " +
                        "by the course author in task specifications. It doesn't have any effect now.",
                default = state.settings.testingFramework,
                options = Framework.values().map { it.alias },
                emptyOption = true,
                onUpdate = { setState { settings = settings.copy(testingFramework = it.ifBlank { null }) } }
        )
    }

    private fun RBuilder.plagiarismAnalysisFilePatternInput() {
        inputComponent(inputId = coursePlagiarismAnalysisFilePatternSetting,
                name = "File pattern for plagiarism analysis",
                description = "Regular expression which will be used to filter repository files " +
                        "for plagiarism analysis. Leave the pattern blank to use a default one " +
                        "(course language source file extensions).",
                default = state.settings.plagiarismFilePattern,
                onUpdate = { setState { settings = settings.copy(plagiarismFilePattern = it.ifBlank { null }) } }
        )
    }

    private fun RBuilder.plagiarismAnalysisBackendSelect() {
        selectComponent(selectId = coursePlagiarismAnalysisBackendSetting,
                name = "Plagiarism analysis backend",
                description = "Plagiarism analysis backend which will be used for all tasks in the course.",
                default = state.settings.plagiarismBackend.name,
                options = PlagiarismBackend.values().map { it.name },
                emptyOption = false,
                onUpdate = {
                    setState {
                        settings = settings.copy(plagiarismBackend = it.ifBlank { null }
                                ?.let { PlagiarismBackend.valueOf(it) }
                                ?: PlagiarismBackend.MOSS)
                    }
                }
        )
    }

    private fun RBuilder.scoreChangeNotificationFlag() {
        flagComponent(inputId = scoreChangeNotificationTemplateSetting,
                name = "Enables notifications on score change",
                description = "Notifications will be sent to students on solutions score change.",
                default = state.settings.notificationOnScoreChange,
                onUpdate = { setState { settings = settings.copy(notificationOnScoreChange = it) } }
        )
    }

    private fun RBuilder.scoreChangeNotificationTemplateInput() {
        inputComponent(inputId = scoreChangeNotificationTemplateSetting,
                name = "Score change notification template",
                description = "Template of the notification that will be sent to students on solutions score change. " +
                        "Use any Markdown constructions and a single %s to inject score to notification. " +
                        "Leave template blank to use a default one.",
                default = state.settings.scoreChangeNotificationTemplate,
                disabled = !state.settings.notificationOnScoreChange,
                onUpdate = {
                    setState {
                        settings = settings.copy(scoreChangeNotificationTemplate = it.ifBlank { null })
                    }
                }
        )
    }

    private suspend fun submitSettingsChanges() {
        credentials?.also { credentials ->
            try {
                val updatedCourse: Course = flaxoClient.updateCourseSetting(credentials, props.course.id,
                        state.settings)
                props.onUpdate(updatedCourse)
                Notifications.success("Course settings have been updated.")
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred while updating course settings.", e)
            }
        }
    }
}
