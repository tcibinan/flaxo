package org.flaxo.rest.service

import org.flaxo.model.data.Course

/**
 * Course validation that can be activated or deactivated and refreshed.
 */
interface CourseValidation {

    /**
     * Activates current validation for [user]'s [course].
     */
    fun activate(course: Course)

    /**
     * Deactivates current validation for [user]'s [course].
     */
    fun deactivate(course: Course)

    /**
     * Refresh results for each solution in course.
     */
    fun refresh(course: Course)
}
