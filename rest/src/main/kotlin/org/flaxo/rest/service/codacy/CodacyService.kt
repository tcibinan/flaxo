package org.flaxo.rest.service.codacy

import org.flaxo.codacy.Codacy
import org.flaxo.model.data.Course
import org.flaxo.model.data.User

/**
 * Codacy service interface.
 */
interface CodacyService {

    /**
     * Returns codacy client authorized wuth the given [codacyToken]
     * for user with the given [githubId].
     */
    fun codacy(githubId: String,
               codacyToken: String
    ): Codacy

    /**
     * Activates codacy pushes and pull request validations
     * of the [user]'s [course].
     */
    fun activateCodacy(user: User,
                       course: Course,
                       githubUserId: String
    )
}