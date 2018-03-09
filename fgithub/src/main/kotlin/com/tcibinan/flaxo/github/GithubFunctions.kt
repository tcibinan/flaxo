package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.GitPayload
import java.io.Reader
import org.kohsuke.github.GHEventPayload as KohsukeGithubEventPayload
import org.kohsuke.github.GitHub as KohsukeGithub

/**
 * Github web hook event parser function.
 */
fun parseGithubEvent(reader: Reader,
                     eventType: String
): GitPayload? =
        when (eventType) {
            "pull_request" ->
                KohsukeGithub.offline()
                        .parseEventPayload(reader, KohsukeGithubEventPayload.PullRequest::class.java)
                        ?.run { GithubPullRequest(this) }
            else -> null
        }
