package com.tcibinan.flaxo.rest.service.git

import com.tcibinan.flaxo.github.GithubInstance

class GithubService(
        private val webHookUrl: String
) : GitService {
    override fun with(credentials: String) = GithubInstance(credentials, webHookUrl)
}