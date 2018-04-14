package org.flaxo.rest.service.travis

import org.flaxo.travis.Travis
import org.flaxo.travis.build.TravisBuild
import java.io.Reader

interface TravisService {
    fun retrieveTravisToken(githubUsername: String, githubToken: String): String
    fun travis(travisToken: String): Travis
    fun parsePayload(reader: Reader): TravisBuild?
}
