package com.tcibinan.flaxo.rest.service.travis

interface TravisService {
    fun retrieveTravisToken(githubUsername: String, githubToken: String): String
    fun travis(travisToken: String): Travis
}

