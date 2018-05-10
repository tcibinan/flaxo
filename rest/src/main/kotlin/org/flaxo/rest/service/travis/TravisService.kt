package org.flaxo.rest.service.travis

import org.flaxo.model.data.Course
import org.flaxo.model.data.User
import org.flaxo.travis.Travis
import org.flaxo.travis.build.TravisBuild
import java.io.Reader

/**
 * Travis service interface.
 */
interface TravisService {

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
     * Parses travis build webhook payload.
     */
    fun parsePayload(reader: Reader): TravisBuild?

    /**
     * Activates [course] repository for [user].
     */
    fun activateTravis(user: User,
                       course: Course,
                       githubToken: String,
                       githubUserId: String
    )
}
