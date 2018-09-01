package org.flaxo.github

import org.flaxo.git.PullRequest

/**
 * Github pull request class.
 */
class GithubPullRequest : PullRequest {

    override val id: Int
    override val baseBranch: String
    override val lastCommitSha: String
    override val mergeCommitSha: String?
    override val authorId: String
    override val receiverId: String
    override val receiverRepositoryName: String
    override val isOpened: Boolean

    constructor(pullRequestEventPayload: RawGithubEventPullRequestPayload) {
        this.id = pullRequestEventPayload.number
        this.baseBranch = pullRequestEventPayload.pullRequest.base.ref
        this.lastCommitSha = pullRequestEventPayload.pullRequest.lastCommit()
        this.mergeCommitSha = pullRequestEventPayload.pullRequest.mergeCommitSha
        this.authorId = pullRequestEventPayload.pullRequest.user.login
        this.receiverId = pullRequestEventPayload.repository.owner.login
        this.receiverRepositoryName = pullRequestEventPayload.repository.name
        this.isOpened = pullRequestEventPayload.action == "opened"
    }

    constructor(pullRequest: RawGithubPullRequest) {
        this.id = pullRequest.number
        this.baseBranch = pullRequest.base.ref
        this.lastCommitSha = pullRequest.lastCommit()
        this.mergeCommitSha = pullRequest.mergeCommitSha
        this.authorId = pullRequest.user.login
        this.receiverId = pullRequest.repository.owner.login
        this.receiverRepositoryName = pullRequest.repository.name
        this.isOpened = pullRequest.state == RawGithubIssueState.OPEN
    }

}

private fun org.kohsuke.github.GHPullRequest.lastCommit(): String =
        listCommits()
                .asList()
                .lastOrNull()
                ?.sha
                ?: throw GithubException("Pull request doesn't have any commits")
