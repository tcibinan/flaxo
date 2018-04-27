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

    fun retrieveTravisToken(githubUsername: String,
                            githubToken: String
    ): String

    fun travis(travisToken: String): Travis

    fun parsePayload(reader: Reader): TravisBuild?

    fun activateTravis(user: User,
                       course: Course,
                       githubToken: String,
                       githubUserId: String
    )
}
