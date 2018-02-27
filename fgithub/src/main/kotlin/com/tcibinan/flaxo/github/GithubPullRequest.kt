package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.PullRequest
import org.kohsuke.github.GHEventPayload

class GithubPullRequest(pullRequest: GHEventPayload.PullRequest)
    : PullRequest {

    override val authorId: String = pullRequest.pullRequest.user.login
    override val receiverId: String = pullRequest.repository.owner.login
    override val receiverRepositoryName: String = pullRequest.repository.name
    override val isOpened: Boolean = pullRequest.action == "opened"

}