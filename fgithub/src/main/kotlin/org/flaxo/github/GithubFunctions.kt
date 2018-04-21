package org.flaxo.github

import org.flaxo.git.GitPayload
import java.io.Reader
import org.kohsuke.github.GHEventPayload as KohsukeGithubEventPayload
import org.kohsuke.github.GitHub as KohsukeGithub

/**
 * Github web hook event parser function.
 */
fun parseGithubEvent(reader: Reader,
                     eventType: String,
                     githubClient: KohsukeGithub
): GitPayload? =
        when (eventType) {
            "pull_request" ->
                githubClient
                        .parseEventPayload(reader, KohsukeGithubEventPayload.PullRequest::class.java)
                        ?.run { GithubPullRequest(this) }
            else -> null
        }
