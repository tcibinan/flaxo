package org.flaxo.frontend.component

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.ButtonType
import kotlinx.html.DIV
import kotlinx.html.js.onClickFunction
import org.flaxo.common.data.Course
import org.flaxo.common.data.CourseLifecycle
import org.flaxo.frontend.Container
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.OnCourseChange
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpException
import org.flaxo.frontend.credentials
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.RDOMBuilder
import react.dom.button
import react.dom.div
import react.dom.h2
import react.dom.small

/**
 * Adds course section.
 */
fun RBuilder.course(selectedCourse: Course, onUpdate: OnCourseChange, onDelete: OnCourseChange) =
        child(org.flaxo.frontend.component.Course::class) {
            attrs {
                this.course = selectedCourse
                this.onUpdate = onUpdate
                this.onDelete = onDelete
            }
        }

private class CourseProps(var course: Course, var onUpdate: OnCourseChange, var onDelete: OnCourseChange) : RProps

private class Course(props: CourseProps) : RComponent<CourseProps, EmptyState>(props) {

    private val flaxoClient: FlaxoClient

    init {
        flaxoClient = Container.flaxoClient
    }

    override fun RBuilder.render() {
        div(classes = "selected-course") {
            h2 {
                +props.course.name
                small { courseLabels(props.course) }
            }
            div(classes = "course-controls") {
                if (props.course.state.lifecycle == CourseLifecycle.INIT) {
                    startCourseButton()
                }
                serviceActivationMenu(props.course)
                courseStatisticsDownloadMenu(props.course)
            }
            courseStatistics(props.course, props.onUpdate)
        }
    }

    private fun RDOMBuilder<DIV>.startCourseButton() {
        button(classes = "btn btn-outline-primary course-control", type = ButtonType.button) {
            attrs {
                onClickFunction = { GlobalScope.launch { startCourse() } }
            }
            +"Start course"
        }
    }

    private suspend fun startCourse() {
        credentials?.also {
            try {
                Notifications.info("Course starting was initiated.")
                val updatedCourse: Course = flaxoClient.startCourse(it, props.course.name)
                props.onUpdate(updatedCourse)
                Notifications.success("Course ${props.course.name} has been started.")
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred while trying to start ${props.course.name} course.", e)
            }
        }
    }

}
