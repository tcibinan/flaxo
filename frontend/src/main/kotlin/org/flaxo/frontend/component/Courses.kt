package org.flaxo.frontend.component

import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import kotlinx.html.ButtonType
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.component.services.codacyModal
import org.flaxo.frontend.component.services.githubModal
import org.flaxo.frontend.component.services.travisModal
import org.flaxo.frontend.credentials
import org.flaxo.common.data.Course
import org.flaxo.common.data.User
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.client.FlaxoHttpException
import react.setState
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.button
import react.dom.div
import react.dom.p

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

private class CoursesState(var courses: List<Course> = emptyList(), var selectedCourse: Course? = null) : RState

private class Courses(props: CoursesProps) : RComponent<CoursesProps, CoursesState>(props) {

    private val flaxoClient: FlaxoClient

    init {
        flaxoClient = Container.flaxoClient
        state = CoursesState()
        GlobalScope.launch { updateCoursesList() }
    }

    override fun RBuilder.render() {
        val selectedCourse = state.selectedCourse
        navigationBar(props.user, props.onLogout, ::deselectCourse)
        if (selectedCourse == null) {
            div(classes = "courses-list") {
                div(classes = "courses-list-container") {
                    state.courses
                            .takeIf { it.isNotEmpty() }
                            ?.forEach { courseCard(it, onSelect = ::selectCourse) }
                            ?: p { +"There are no courses yet." }
                }
                button(classes = "btn btn-outline-primary btn-block", type = ButtonType.button) {
                    attrs {
                        attributes["data-toggle"] = "modal"
                        attributes["data-target"] = "#$COURSE_CREATION_MODAL_ID"
                        disabled = !props.user.isGithubAuthorized
                    }
                    +"Create course"
                }
            }
        } else {
            course(selectedCourse, onUpdate = { GlobalScope.launch { updateCoursesList() } }, onDelete = ::deselectCourse)
        }
        githubModal(props.user)
        travisModal(props.user)
        codacyModal(props.user)
        plagiarismModal()
        courseCreationModal(onCourseCreation = { GlobalScope.launch { updateCoursesList() } })
    }

    private suspend fun updateCoursesList() {
        credentials?.also { credentials ->
            try {
                val courses = flaxoClient.getUserCourses(credentials, props.user.nickname)
                val selectedCourse = state.selectedCourse?.let { previouslySelectedCourse ->
                    courses.find { it.name == previouslySelectedCourse.name }
                }
                setState {
                    this.selectedCourse = selectedCourse
                    this.courses = courses
                }
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred while retrieving courses list.", e)
            }
        }
    }

    private fun deselectCourse() {
        setState { selectedCourse = null }
    }

    private fun selectCourse(course: Course) {
        setState { selectedCourse = course }
    }

}
