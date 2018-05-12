package org.flaxo.rest.service.git

import org.flaxo.git.GitPayload
import org.flaxo.github.Github
import org.flaxo.github.GithubException
import org.flaxo.github.parseGithubEvent
import java.io.Reader
import org.kohsuke.github.GitHub as KohsukeGithub

/**
 * Github integration service.
 */
class GithubService(
        private val webHookUrl: String
) : GitService {

    override fun with(credentials: String) =
            Github({ KohsukeGithub.connectUsingOAuth(credentials) }, webHookUrl)

    override fun parsePayload(reader: Reader,
                              headers: Map<String, List<String>>
    ): GitPayload? {
        val types = headers["x-github-event"].orEmpty()
                .also {
                    if (it.isEmpty()) throw GithubException("Github payload type wasn't found in headers.")
                }
        return parseGithubEvent(reader, types.first(), KohsukeGithub.connectAnonymously())
    }
}