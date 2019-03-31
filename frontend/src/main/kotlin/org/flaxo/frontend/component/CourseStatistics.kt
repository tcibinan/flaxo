package org.flaxo.frontend.component

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.id
import kotlinx.html.role
import org.flaxo.common.data.Course
import org.flaxo.common.data.CourseStatistics
import org.flaxo.common.data.Task
import org.flaxo.frontend.Container
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.OnCourseChange
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpException
import org.flaxo.frontend.credentials
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.a
import react.dom.div
import react.dom.li
import react.dom.ul
import react.setState

/**
 * Adds course summary statistics table.
 */
fun RBuilder.courseStatistics(course: Course, onUpdate: OnCourseChange) =
        child(org.flaxo.frontend.component.CourseStatistics::class) {
            attrs {
                this.course = course
                this.onUpdate = onUpdate
            }
        }

private class CourseStatisticsProps(var course: Course, var onUpdate: OnCourseChange) : RProps

private class CourseStatisticsState(var courseStatistics: CourseStatistics?) : RState

private class CourseStatistics(props: CourseStatisticsProps)
    : RComponent<CourseStatisticsProps, CourseStatisticsState>(props) {

    private val courseSummaryTab = "courseSummaryTab-${props.course.id}"
    private val courseStatisticsContent = "courseStatisticsContent-${props.course.id}"
    private val courseSummaryContent = "courseSummaryContent-${props.course.id}"
    private val courseStatisticsNavigation = "courseStatisticsNavigation-${props.course.id}"
    private val taskContentIdTemplate = "taskContent-${props.course.id}"
    private val taskTabIdTemplate = "taskTab-${props.course.id}"

    private val flaxoClient: FlaxoClient

    init {
        flaxoClient = Container.flaxoClient
        state.apply {
            courseStatistics = null
        }
        GlobalScope.launch {
            credentials?.also {
                try {
                    val courseStatistics =
                            flaxoClient.getCourseStatistics(it, props.course.user.name, props.course.name)
                    setState { this.courseStatistics = courseStatistics }
                } catch (e: FlaxoHttpException) {
                    console.log(e)
                    Notifications.error("Error occurred while retrieving course statistics.", e)
                }
            }
        }
    }

    override fun RBuilder.render() {
        div(classes = "course-tabs") {
            ul(classes = "nav nav-tabs") {
                attrs {
                    id = courseStatisticsNavigation
                    role = "tablist"
                }
                li("nav-item") {
                    a(classes = "nav-link active", href = "#$courseSummaryContent") {
                        attrs {
                            id = courseSummaryTab
                            role = "tab"
                            attributes["data-toggle"] = "tab"
                            attributes["aria-controls"] = courseSummaryContent
                            attributes["aria-selected"] = "true"
                        }
                        +"Course summary"
                    }
                }
                state.courseStatistics?.tasks
                        ?.sortedBy { it.branch }
                        ?.forEach { task ->
                            li("nav-item") {
                                a(classes = "nav-link", href = "#${taskContentIdTemplate + task.branch}") {
                                    attrs {
                                        id = taskTabIdTemplate + task.branch
                                        role = "tab"
                                        attributes["data-toggle"] = "tab"
                                        attributes["aria-controls"] = taskContentIdTemplate + task.branch
                                        attributes["aria-selected"] = "false"
                                    }
                                    +task.branch
                                }
                            }
                        }
            }
        }
        div(classes = "tab-content") {
            attrs { id = courseStatisticsContent }
            div(classes = "tab-pane show active") {
                attrs {
                    id = courseSummaryContent
                    role = "tabpanel"
                    attributes["aria-labelledby"] = courseSummaryTab
                }
                state.courseStatistics?.also {
                    courseSummary(props.course, it, props.onUpdate, ::updateStatistics)
                }
            }
            state.courseStatistics?.tasks
                    ?.sortedBy { it.branch }
                    ?.forEach { task ->
                        div(classes = "tab-pane") {
                            attrs {
                                id = taskContentIdTemplate + task.branch
                                role = "tabpanel"
                                attributes["aria-labelledby"] = taskTabIdTemplate + task.branch
                            }
                            task(props.course, task, onUpdate = ::updateTask)
                        }
                    }

        }
    }

    private fun updateStatistics(courseStatistics: CourseStatistics) = setState {
        this.courseStatistics = courseStatistics
    }

    private fun updateTask(task: Task) = setState {
        val updatedTasks = courseStatistics?.tasks.orEmpty().map { if (it.id == task.id) task else it }
        courseStatistics = courseStatistics?.copy(tasks = updatedTasks)
    }
}
