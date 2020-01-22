package org.flaxo.rest.manager.gitplag

import org.flaxo.common.data.CourseSettings
import org.flaxo.model.CourseView
import org.flaxo.model.DataManager
import org.flaxo.rest.manager.CourseAccessDeniedException
import org.flaxo.rest.manager.CourseNotFoundException
import org.flaxo.rest.manager.UserNotFoundException
import org.flaxo.rest.manager.course.CourseManager

/**
 * Gitplag course manager implementation.
 */
class GitplagCourseManager(private val courseManager: CourseManager,
                           private val dataManager: DataManager,
                           private val gitplagManager: GitplagManager) : CourseManager {
    override fun updateSettings(userName: String, id: Long, settings: CourseSettings): CourseView {
        val user = dataManager.getUser(userName) ?: throw UserNotFoundException(userName)
        val course = dataManager.getCourse(id) ?: throw CourseNotFoundException(id)
        if (course.user != user) throw CourseAccessDeniedException(user.name, id)

        val reloadRepositoryFiles = course.settings.plagiarismFilePattern != settings.plagiarismFilePattern

        val courseView = courseManager.updateSettings(userName, id, settings)

        gitplagManager.refresh(course)
        if (reloadRepositoryFiles) gitplagManager.reloadFiles(course)

        return courseView
    }
}