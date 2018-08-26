package org.flaxo.rest.manager.github

import org.flaxo.git.Git
import org.flaxo.git.GitPayload
import java.io.Reader

/**
 * Github manager.
 */
interface GithubManager {

    /**
     * Returns a git client with the given [credentials].
     */
    fun with(credentials: String): Git

    /**
     * Parses git webhook and returns a git payload or null if something went wrong.
     */
    fun parsePayload(reader: Reader, headers: Map<String, List<String>>): GitPayload?
}