package com.tcibinan.flaxo.rest.service.git

import com.tcibinan.flaxo.git.GitPayload
import com.tcibinan.flaxo.github.Github
import com.tcibinan.flaxo.github.parseGithubEvent
import java.io.Reader

class GithubService(
        private val webHookUrl: String
) : GitService {

    override fun with(credentials: String) = Github(credentials, webHookUrl)

    override fun parsePayload(reader: Reader, headers: Map<String, List<String>>): GitPayload? {
        val types = headers["X-GitHub-Event"].orEmpty()
                .also {
                    if (it.isEmpty()) throw Exception("Github payload type wasn't found in headers.")
                }
        return parseGithubEvent(reader, types.first())
    }
}