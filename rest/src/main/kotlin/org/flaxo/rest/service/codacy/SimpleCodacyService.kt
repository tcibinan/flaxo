package org.flaxo.rest.service.codacy

import org.flaxo.codacy.Codacy
import org.flaxo.codacy.CodacyClient
import org.flaxo.codacy.SimpleCodacy

/**
 * Codacy service basic implementation.
 */
class SimpleCodacyService(private val client: CodacyClient
) : CodacyService {

    override fun codacy(githubId: String, codacyToken: String): Codacy =
            SimpleCodacy(githubId, codacyToken, client)

}