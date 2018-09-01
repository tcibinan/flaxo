package org.flaxo.github

import org.flaxo.git.GitPayload
import java.io.Reader

/**
 * Github web hook event parser function.
 */
fun parseGithubEvent(reader: Reader,
                     eventType: String,
                     githubClient: RawGithub
): GitPayload? =
        when (eventType) {
            "pull_request" ->
                githubClient
                        .parseEventPayload(reader, RawGithubEventPullRequestPayload::class.java)
                        ?.run { GithubPullRequest(this) }
            else -> null
        }
