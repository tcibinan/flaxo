package org.flaxo.frontend.component

import kotlinx.coroutines.experimental.launch
import kotlinx.html.ButtonType
import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.credentials
import org.flaxo.common.Course
import org.flaxo.common.CourseLifecycle
import org.flaxo.frontend.Notifications
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.button
import react.dom.div
import react.dom.h2
import react.dom.small

fun RBuilder.course(selectedCourse: Course, onUpdate: () -> Unit, onDelete: () -> Unit) =
        child(org.flaxo.frontend.component.Course::class) {
            attrs {
                this.course = selectedCourse
                this.onUpdate = onUpdate
                this.onDelete = onDelete
            }
        }

class CourseProps(var course: Course,
                  var onUpdate: () -> Unit,
                  var onDelete: () -> Unit) : RProps

class Course(props: CourseProps) : RComponent<CourseProps, EmptyState>(props) {

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
                button(classes = "btn btn-outline-primary course-control", type = ButtonType.button) {
                    attrs {
                        onClickFunction = { launch { startCourse() } }
                        disabled = props.course.state.lifecycle == CourseLifecycle.RUNNING
                    }
                    +"Start course"
                }
                serviceActivationMenu(props.course)
                button(classes = "btn btn-outline-primary course-control", type = ButtonType.button) {
                    attrs {
                        onClickFunction = { launch { analysePlagiarism() } }
                        disabled = props.course.state.lifecycle != CourseLifecycle.RUNNING
                    }
                    +"Analyse plagiarism"
                }
                courseStatisticsDownloadMenu(props.course)
            }
            courseStatistics(props.course)
        }
    }

    private suspend fun startCourse() {
        credentials?.also {
            try {
                Notifications.info("Course starting was initiated.")
                flaxoClient.startCourse(it, props.course.name)
                props.onUpdate()
                Notifications.success("Course ${props.course.name} has been started.")
            } catch (e: Exception) {
                console.log(e)
                Notifications.error("Error occurred while trying to start ${props.course.name} course.")
            }
        }
    }

    private suspend fun analysePlagiarism() {
        credentials?.also {
            try {
                Notifications.info("Course ${props.course.name} plagiarism analysis has been started.")
                flaxoClient.analysePlagiarism(it, props.course.name)
                Notifications.success("Plagiarism analysis for course ${props.course.name} " +
                        "has been started.")
            } catch (e: Exception) {
                console.log(e)
                Notifications.error("Error occurred while starting plagiarism analysis " +
                        "for ${props.course.name} course.")
            }
        }
    }

}
