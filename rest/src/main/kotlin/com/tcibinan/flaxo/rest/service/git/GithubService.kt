package com.tcibinan.flaxo.rest.service.git

import com.tcibinan.flaxo.git.GitPayload
import com.tcibinan.flaxo.github.Github
import com.tcibinan.flaxo.github.parseGithubEvent
import javax.servlet.http.HttpServletRequest

class GithubService(
        private val webHookUrl: String
) : GitService {

    override fun with(credentials: String) = Github(credentials, webHookUrl)

    override fun parsePayload(request: HttpServletRequest): GitPayload? {
        return parseGithubEvent(
                request.reader,
                request.getHeader("X-GitHub-Event")
        )
    }
}