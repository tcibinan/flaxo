package org.flaxo.frontend.component

import kotlinx.coroutines.experimental.launch
import kotlinx.html.classes
import kotlinx.html.id
import kotlinx.html.role
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.credentials
import org.flaxo.frontend.data.Course
import org.flaxo.frontend.data.CourseStatistics
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.a
import react.dom.div
import react.dom.li
import react.dom.ul
import react.setState

fun RBuilder.courseStatistics(course: Course) =
        child(org.flaxo.frontend.component.CourseStatistics::class) {
            attrs {
                this.course = course
            }
        }

class CourseStatisticsProps(var course: Course) : RProps
class CourseStatisticsState(var activeTab: String,
                            var courseStatistics: CourseStatistics?) : RState

class CourseStatistics(props: CourseStatisticsProps)
    : RComponent<CourseStatisticsProps, CourseStatisticsState>(props) {

    private companion object {
        const val COURSE_STATISTICS_NAVIGATION_ID = "courseStatisticsNavigation"
        const val COURSE_SUMMARY_TAB_ID = "courseSummaryTab"
        // TODO 15.08.18: Two courses may have tabs with the same id
        const val TASK_TAB_ID_TEMPLATE = "taskTab-"
        const val COURSE_STATISTICS_CONTENT_ID = "courseStatisticsContent"
        const val COURSE_SUMMARY_CONTENT_ID = "courseSummaryContent"
        // TODO 15.08.18: Two courses may have tab contents with the same id
        const val TASK_CONTENT_ID_TEMPLATE = "taskContent-"
    }

    private val flaxoClient: FlaxoClient

    init {
        flaxoClient = Container.flaxoClient
        state.apply {
            activeTab = COURSE_SUMMARY_TAB_ID
            courseStatistics = null
        }
        launch {
            credentials?.also {
                try {
                    val courseStatistics =
                            flaxoClient.getCourseStatistics(it, props.course.user.nickname, props.course.name)
                    setState { this.courseStatistics = courseStatistics }
                } catch (e: Exception) {
                    // TODO 15.08.18: notify user that course statistics retrieving has failed
                    console.log(e)
                }
            }
        }
    }

    override fun RBuilder.render() {
        div(classes = "course-tabs") {
            ul(classes = "nav nav-tabs") {
                attrs {
                    id = COURSE_STATISTICS_NAVIGATION_ID
                    role = "tablist"
                }
                li("nav-item") {
                    a(classes = "nav-link active", href = "#") {
                        attrs {
                            id = COURSE_SUMMARY_TAB_ID
                            role = "tab"
                            attributes["data-toggle"] = "tab"
                            attributes["aria-controls"] = id
                            if (state.activeTab == id) classes += "active"
                        }
                        +"Course summary"
                    }
                }
                state.courseStatistics?.tasks
                        ?.sortedBy { it.branch }
                        ?.forEachIndexed { index, task ->
                            li("nav-item") {
                                a(classes = "nav-link", href = "#") {
                                    attrs {
                                        id = TASK_TAB_ID_TEMPLATE + task.branch
                                        role = "tab"
                                        attributes["data-toggle"] = "tab"
                                        attributes["aria-controls"] = id
                                        if (state.activeTab == id) classes += "active"
                                    }
                                    +task.branch
                                }
                            }
                        }
            }
        }
        div(classes = "tab-content") {
            attrs { id = COURSE_STATISTICS_CONTENT_ID }
            div(classes = "tab-pane fade") {
                attrs {
                    id = COURSE_SUMMARY_CONTENT_ID
                    role = "tabpanel"
                    attributes["aria-labelledby"] = COURSE_SUMMARY_TAB_ID
                }
//                courseSummary(props.course, state.courseStatistics)
            }
            state.courseStatistics?.tasks
                    ?.sortedBy { it.branch }
                    ?.forEachIndexed { index, task ->
                        div(classes = "tab-pane fade") {
                            attrs {
                                id = TASK_CONTENT_ID_TEMPLATE + task.branch
                                role = "tabpanel"
                                attributes["aria-labelledby"] = TASK_TAB_ID_TEMPLATE + task.branch
                            }
//                            task(props.course, task)
                        }
                    }

        }
    }
}
