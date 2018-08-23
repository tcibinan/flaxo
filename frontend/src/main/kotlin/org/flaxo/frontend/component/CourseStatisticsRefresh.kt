package org.flaxo.frontend.component

import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.credentials
import org.flaxo.frontend.data.Course
import org.flaxo.frontend.data.CourseLifecycle
import org.flaxo.frontend.wrapper.NotificationManager
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
            NotificationManager.success("Course statistics synchronization has been finished.")
        } catch (e: Exception) {
            console.log(e)
            NotificationManager.error("Error occurred during ${course.name} course statistics synchronization.")
        }
    }
}