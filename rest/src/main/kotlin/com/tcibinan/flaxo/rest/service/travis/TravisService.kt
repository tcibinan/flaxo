package com.tcibinan.flaxo.rest.service.travis

import com.tcibinan.flaxo.travis.Travis

interface TravisService {
    fun retrieveTravisToken(githubUsername: String, githubToken: String): String
    fun travis(travisToken: String): Travis
}
