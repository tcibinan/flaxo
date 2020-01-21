package org.flaxo.rest.manager.course

import org.flaxo.common.data.CourseSettings
import org.flaxo.model.CourseView
import org.flaxo.model.DataManager
import org.flaxo.model.data.model
import org.flaxo.rest.manager.CourseAccessDeniedException
import org.flaxo.rest.manager.CourseNotFoundException
import org.flaxo.rest.manager.UserNotFoundException
import org.flaxo.rest.manager.gitplag.GitplagManager

/**
 * Basic course manager implementation.
 */
class BasicCourseManager(private val dataManager: DataManager,
                         private val gitplagManager: GitplagManager) : CourseManager {

    override fun updateSettings(userName: String, id: Long, settings: CourseSettings): CourseView {
        val user = dataManager.getUser(userName) ?: throw UserNotFoundException(userName)
        val course = dataManager.getCourse(id) ?: throw CourseNotFoundException(id)
        if (course.user != user) throw CourseAccessDeniedException(user.name, id)

        val reloadRepositoryFiles = course.settings.filePatterns != settings.filePatterns

        val view = dataManager.updateCourse(course.copy(settings = settings.model())).view()

        gitplagManager.refresh(course)
        if (reloadRepositoryFiles) gitplagManager.reloadFiles(course)

        return view
    }
}
