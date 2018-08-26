package org.flaxo.rest.manager.codacy

import org.flaxo.codacy.Codacy
import org.flaxo.rest.manager.ValidationManager

/**
 * Codacy manager.
 */
interface CodacyManager : ValidationManager {

    /**
     * Returns codacy client authorized wuth the given [codacyToken]
     * for user with the given [githubId].
     */
    fun codacy(githubId: String, codacyToken: String): Codacy
}