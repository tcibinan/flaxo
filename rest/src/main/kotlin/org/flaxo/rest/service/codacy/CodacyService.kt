package org.flaxo.rest.service.codacy

import org.flaxo.codacy.Codacy
import org.flaxo.rest.service.ComposingIntegratedService

/**
 * Codacy service interface.
 */
interface CodacyService: ComposingIntegratedService {

    /**
     * Returns codacy client authorized wuth the given [codacyToken]
     * for user with the given [githubId].
     */
    fun codacy(githubId: String,
               codacyToken: String
    ): Codacy
}