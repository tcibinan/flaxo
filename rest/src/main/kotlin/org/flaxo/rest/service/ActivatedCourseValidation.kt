package org.flaxo.rest.service

import org.flaxo.model.data.Course
import org.flaxo.model.data.User

/**
 * Course validation that can be activated.
 */
interface ActivatedCourseValidation {

    /**
     * Activates current validation for [user]'s [course].
     */
    fun activateFor(user: User,
                    course: Course
    )
}
