package org.flaxo.rest.service.travis

import org.flaxo.rest.service.CourseValidation
import org.flaxo.travis.Travis
import org.flaxo.travis.build.TravisBuild
import java.io.Reader

/**
 * Travis service interface.
 */
interface TravisService : CourseValidation {

    /**
     * Retrieves travis access token.
     *
     * May cause travis account *sync*.
     */
    fun retrieveTravisToken(githubUsername: String,
                            githubToken: String
    ): String

    /**
     * Returns travis client authorized with the given [travisToken].
     */
    fun travis(travisToken: String): Travis

    /**
     * Parses travis build webhook payload from [reader].
     */
    fun parsePayload(reader: Reader): TravisBuild?

}
