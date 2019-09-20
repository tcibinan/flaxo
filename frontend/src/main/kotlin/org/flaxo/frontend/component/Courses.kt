package org.flaxo.frontend.component

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.ButtonType
import org.flaxo.common.data.Course
import org.flaxo.common.data.User
import org.flaxo.frontend.Container
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpException
import org.flaxo.frontend.component.services.codacyModal
import org.flaxo.frontend.component.services.githubModal
import org.flaxo.frontend.component.services.travisModal
import org.flaxo.frontend.credentials
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.button
import react.dom.div
import react.dom.p
import react.setState

/**
 * Adds courses list.
 */
fun RBuilder.courses(user: User, onLogout: () -> Unit) = child(Courses::class) {
    attrs {
        this.user = user
        this.onLogout = onLogout
    }
}

private class CoursesProps(var user: User, var onLogout: () -> Unit) : RProps

private class CoursesState(var courses: List<Course> = emptyList(), var selectedCourseId: Long? = null) : RState

private class Courses(props: CoursesProps) : RComponent<CoursesProps, CoursesState>(props) {

    private val flaxoClient: FlaxoClient

    init {
        flaxoClient = Container.flaxoClient
        state = CoursesState()
        GlobalScope.launch { updateCoursesList() }
    }

    private suspend fun updateCoursesList() {
        credentials?.also { credentials ->
            try {
                val courses = flaxoClient.getUserCourses(credentials, props.user.name)
                setState { this.courses = courses }
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred while retrieving courses list.", e)
            }
        }
    }

    override fun RBuilder.render() {
        val selectedCourse = state.courses.find { it.id == state.selectedCourseId }
        navigationBar(props.user, props.onLogout, ::deselectCourse)
        if (selectedCourse == null) {
            div(classes = "courses-list") {
                div(classes = "courses-list-container") {
                    state.courses
                            .takeIf { it.isNotEmpty() }
                            ?.forEach { courseCard(it, onSelect = ::selectCourse) }
                            ?: p { +"There are no courses yet." }
                }
                div(classes = "courses-controls") {
                    button(classes = "btn btn-outline-primary courses-create-btn", type = ButtonType.button) {
                        attrs {
                            attributes["data-toggle"] = "modal"
                            attributes["data-target"] = "#$COURSE_CREATION_MODAL_ID"
                            disabled = !props.user.githubAuthorized
                        }
                        +"Create course"
                    }
                    button(classes = "btn btn-outline-primary courses-import-btn", type = ButtonType.button) {
                        attrs {
                            attributes["data-toggle"] = "modal"
                            attributes["data-target"] = "#$COURSE_IMPORT_MODAL_ID"
                            disabled = !props.user.githubAuthorized
                        }
                        +"Import existing course"
                    }
                }
            }
        } else {
            course(selectedCourse, onUpdate = ::updateCourse, onDelete = ::deleteCourse)
        }
        githubModal(props.user)
        travisModal(props.user)
        codacyModal(props.user)
        plagiarismModal()
        courseCreationModal(onCreate = ::addCourse)
        courseImportModal(onImport = ::addCourse)
    }

    private fun deselectCourse() = setState { selectedCourseId = null }

    private fun selectCourse(course: Course) = setState { selectedCourseId = course.id }

    private fun deleteCourse(course: Course) = setState {
        selectedCourseId = null
        courses = courses.filter { it.id != course.id }
    }

    private fun updateCourse(course: Course) = setState {
        courses = courses.map { if (it.id == course.id) course else it }
    }

    private fun addCourse(course: Course) = setState { courses += course }
}
