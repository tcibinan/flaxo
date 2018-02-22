package com.tcibinan.flaxo.rest.service.travis

import com.tcibinan.flaxo.travis.Travis
import com.tcibinan.flaxo.travis.build.TravisBuild
import java.io.Reader

interface TravisService {
    fun retrieveTravisToken(githubUsername: String, githubToken: String): String
    fun travis(travisToken: String): Travis
    fun parsePayload(reader: Reader): TravisBuild?
}
