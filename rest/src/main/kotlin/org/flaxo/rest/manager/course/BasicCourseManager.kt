package org.flaxo.rest.manager.course

import org.flaxo.common.data.CourseSettings
import org.flaxo.model.CourseView
import org.flaxo.model.DataManager
import org.flaxo.model.data.model
import org.flaxo.rest.manager.CourseAccessDeniedException
import org.flaxo.rest.manager.CourseNotFoundException
import org.flaxo.rest.manager.UserNotFoundException

/**
 * Basic course manager implementation.
 */
class BasicCourseManager(private val dataManager: DataManager) : CourseManager {

    override fun updateSettings(userName: String, id: Long, settings: CourseSettings): CourseView {
        val user = dataManager.getUser(userName) ?: throw UserNotFoundException(userName)
        val course = dataManager.getCourse(id) ?: throw CourseNotFoundException(id)
        if (course.user != user) throw CourseAccessDeniedException(user.name, id)

        return dataManager.updateCourse(course.copy(settings = settings.model())).view()
    }
}
