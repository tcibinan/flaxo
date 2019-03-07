package org.flaxo.frontend.component

import kotlinx.coroutines.experimental.launch
import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.credentials
import org.flaxo.common.data.Course
import org.flaxo.common.data.CourseLifecycle
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.client.FlaxoHttpException
import react.RBuilder
import react.dom.button
import react.dom.i

/**
 * Adds course statistics refresh button.
 */
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
            Notifications.info("Course statistics refreshing was initiated.")
            Container.flaxoClient.syncCourse(it, course.name)
            Notifications.success("Course statistics synchronization has been finished.")
        } catch (e: FlaxoHttpException) {
            console.log(e)
            Notifications.error("Error occurred during ${course.name} course statistics synchronization.", e)
        }
    }
}
