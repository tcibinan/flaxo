package org.flaxo.rest.manager

import org.flaxo.model.data.Course

/**
 * Validation manager that can activate, deactivate and refresh validation for concrete course.
 */
interface ValidationManager {

    /**
     * Activates current validation for [course].
     */
    fun activate(course: Course)

    /**
     * Deactivates current validation for [course].
     */
    fun deactivate(course: Course)

    /**
     * Refresh results for each solution in [course].
     */
    fun refresh(course: Course)
}
