package com.tcibinan.flaxo.rest.services

import com.tcibinan.flaxo.github.GithubInstance

class GithubService(
        private val webHookUrl: String
) : GitService {
    override fun with(credentials: String) = GithubInstance(credentials, webHookUrl)
}