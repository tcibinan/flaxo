package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.GitPayload
import org.kohsuke.github.GHEventPayload
import java.io.Reader
import org.kohsuke.github.GitHub  as KohsukeGit

internal val github = KohsukeGit.offline()

fun parseGithubEvent(reader: Reader, eventType: String): GitPayload? {
    return when (eventType) {
        "pull_request" -> {
            github.parseEventPayload(reader, GHEventPayload.PullRequest::class.java)
                    ?.run { GithubPullRequest(this) }
        }
        else -> null
    }
}
