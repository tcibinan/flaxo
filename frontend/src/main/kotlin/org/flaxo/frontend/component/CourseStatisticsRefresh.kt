package org.flaxo.frontend.component

import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.credentials
import org.flaxo.frontend.data.Course
import org.flaxo.frontend.data.CourseLifecycle
import react.RBuilder
import react.dom.button
import react.dom.i


fun RBuilder.courseStatisticsRefresh(course: Course) {
    button(classes = "btn btn-outline-info icon-btn") {
        attrs {
            onClickFunction = { synchronizeCourseStatistics(course) }
            disabled = course.state.lifecycle == CourseLifecycle.INIT
        }
        i(classes = "material-icons") { +"refresh" }
    }
}

private fun synchronizeCourseStatistics(course: Course) {
    credentials?.also {
        try {
            Container.flaxoClient.syncCourse(it, course.name)
            // TODO 18.08.18: notify user that course statistics has been synchronized
        } catch (e: Exception) {
            console.log(e)
            // TODO 18.08.18: notify user that course statistics synchronization failed
        }
    }
}