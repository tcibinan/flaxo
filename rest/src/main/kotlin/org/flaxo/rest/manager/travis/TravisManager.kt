package org.flaxo.rest.manager.travis

import org.flaxo.rest.manager.ValidationManager
import org.flaxo.travis.Travis
import org.flaxo.travis.TravisBuild
import java.io.Reader

/**
 * Travis manager.
 */
interface TravisManager : ValidationManager {

    /**
     * Retrieves travis access token.
     *
     * May cause travis account *sync*.
     */
    fun retrieveTravisToken(githubUsername: String, githubToken: String): String

    /**
     * Returns travis client authorized with the given [travisToken].
     */
    fun travis(travisToken: String): Travis

    /**
     * Parses travis build webhook payload from [reader].
     */
    fun parsePayload(reader: Reader): TravisBuild?

}
