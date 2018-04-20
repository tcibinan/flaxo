package org.flaxo.rest.service.codacy

import org.flaxo.codacy.Codacy

/**
 * Codacy service interface.
 */
interface CodacyService {

    fun codacy(githubId: String,
               codacyToken: String
    ): Codacy
}