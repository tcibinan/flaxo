package org.flaxo.rest.manager.course

import org.flaxo.common.data.CourseSettings
import org.flaxo.model.CourseView

/**
 * Course manager.
 *
 * It provides all kinds of operations for courses management.
 */
interface CourseManager {

    /**
     * Updates course settings.
     *
     * @param userName Course owner name.
     * @param id Updating course id.
     * @param settings Updated course settings.
     */
    fun updateSettings(userName: String, id: Long, settings: CourseSettings): CourseView
}
