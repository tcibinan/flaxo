package org.flaxo.rest.service

import org.flaxo.model.data.Course
import org.flaxo.model.data.User

interface ComposingIntegratedService {

    /**
     * Activates validations of the current integration service
     * for [course] repository of the [user].
     */
    fun activateServiceFor(user: User,
                           course: Course
    )
}
