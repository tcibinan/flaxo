package org.flaxo.frontend.component

import kotlinx.coroutines.experimental.launch
import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.credentials
import org.flaxo.common.Course
import org.flaxo.common.CourseLifecycle
import org.flaxo.frontend.wrapper.NotificationManager
import react.RBuilder
import react.dom.button
import react.dom.i


fun RBuilder.courseStatisticsRefresh(course: Course) {
    button(classes = "btn btn-outline-info icon-btn") {
        attrs {
            onClickFunction = { launch { synchronizeCourseStatistics(course) } }
            disabled = course.state.lifecycle == CourseLifecycle.INIT
        }
        i(classes = "material-icons") { +"refresh" }
    }
}

private suspend fun synchronizeCourseStatistics(course: Course) {
    credentials?.also {
        try {
            NotificationManager.info("Course statistics refreshing was initiated.")
            Container.flaxoClient.syncCourse(it, course.name)
            NotificationManager.success("Course statistics synchronization has been finished.")
        } catch (e: Exception) {
            console.log(e)
            NotificationManager.error("Error occurred during ${course.name} course statistics synchronization.")
        }
    }
}
