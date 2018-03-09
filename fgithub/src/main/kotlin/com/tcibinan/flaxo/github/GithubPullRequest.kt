package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.PullRequest
import org.kohsuke.github.GHEventPayload as KohsukeGithubEventPayload
import org.kohsuke.github.GHIssueState as KohsukeGithubIssueState
import org.kohsuke.github.GHPullRequest as KohsukeGithubPullRequest

/**
 * Github pull request class.
 */
class GithubPullRequest : PullRequest {

    constructor(pullRequestEventPayload: KohsukeGithubEventPayload.PullRequest) {
        this.authorId = pullRequestEventPayload.pullRequest.user.login
        this.receiverId = pullRequestEventPayload.repository.owner.login
        this.receiverRepositoryName = pullRequestEventPayload.repository.name
        this.isOpened = pullRequestEventPayload.action == "opened"
    }

    constructor(pullRequest: KohsukeGithubPullRequest) {
        this.authorId = pullRequest.user.login
        this.receiverId = pullRequest.repository.owner.login
        this.receiverRepositoryName = pullRequest.repository.name
        this.isOpened = pullRequest.state == KohsukeGithubIssueState.OPEN
    }

    override val authorId: String
    override val receiverId: String
    override val receiverRepositoryName: String
    override val isOpened: Boolean

}