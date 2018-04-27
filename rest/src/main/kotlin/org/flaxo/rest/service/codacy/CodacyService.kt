package org.flaxo.rest.service.codacy

import org.flaxo.codacy.Codacy
import org.flaxo.model.data.Course
import org.flaxo.model.data.User

/**
 * Codacy service interface.
 */
interface CodacyService {

    fun codacy(githubId: String,
               codacyToken: String
    ): Codacy

    fun activateCodacy(user: User,
                       course: Course,
                       githubUserId: String
    )
}