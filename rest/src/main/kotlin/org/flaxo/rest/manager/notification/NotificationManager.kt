package org.flaxo.rest.manager.notification

import org.flaxo.model.data.Course

interface NotificationManager {
    fun notify(course: Course, notifications: List<SolutionNotification>)
}
