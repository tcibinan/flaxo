package org.flaxo.rest.service.codacy

import org.flaxo.codacy.Codacy
import org.flaxo.rest.service.ActivatedCourseValidation

/**
 * Codacy service interface.
 */
interface CodacyService: ActivatedCourseValidation {

    /**
     * Returns codacy client authorized wuth the given [codacyToken]
     * for user with the given [githubId].
     */
    fun codacy(githubId: String,
               codacyToken: String
    ): Codacy
}