package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.PullRequest
import org.kohsuke.github.GHEventPayload
import org.kohsuke.github.GHIssueState
import org.kohsuke.github.GHPullRequest

class GithubPullRequest : PullRequest {

    constructor(payload: GHEventPayload.PullRequest) {
        this.authorId = payload.pullRequest.user.login
        this.receiverId = payload.repository.owner.login
        this.receiverRepositoryName = payload.repository.name
        this.isOpened = payload.action == "opened"
    }

    constructor(pullRequest: GHPullRequest) {
        this.authorId = pullRequest.user.login
        this.receiverId = pullRequest.repository.owner.login
        this.receiverRepositoryName = pullRequest.repository.name
        this.isOpened = pullRequest.state == GHIssueState.OPEN
    }

    override val authorId: String
    override val receiverId: String
    override val receiverRepositoryName: String
    override val isOpened: Boolean

}