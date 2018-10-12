package org.flaxo.github

import org.flaxo.git.PullRequest

private const val UNDEFINED = "undefined"

/**
 * Github pull request class.
 */
class GithubPullRequest : PullRequest {

    // These 4 id fields are conceptually wrong in the GitHub REST implementation
    // It is temporary workaround until GitHub REST will be completely replaced with the GraphQL one
    override val id: String  = UNDEFINED
    override val authorId: String = UNDEFINED
    override val receiverId: String = UNDEFINED
    override val receiverRepositoryId: String = UNDEFINED

    override val number: Int
    override val sourceBranch: String
    override val targetBranch: String
    override val lastCommitSha: String
    override val mergeCommitSha: String?
    override val authorLogin: String
    override val receiverLogin: String
    override val receiverRepositoryName: String
    override val isOpened: Boolean

    constructor(pullRequestEventPayload: RawGithubEventPullRequestPayload) {
        number = pullRequestEventPayload.number
        sourceBranch = pullRequestEventPayload.pullRequest.head.ref
        targetBranch = pullRequestEventPayload.pullRequest.base.ref
        lastCommitSha = pullRequestEventPayload.pullRequest.lastCommit()
        mergeCommitSha = pullRequestEventPayload.pullRequest.mergeCommitSha
        authorLogin = pullRequestEventPayload.pullRequest.user.login
        receiverLogin = pullRequestEventPayload.repository.owner.login
        receiverRepositoryName = pullRequestEventPayload.repository.name
        isOpened = pullRequestEventPayload.action == "opened"
    }

    constructor(pullRequest: RawGithubPullRequest) {
        number = pullRequest.number
        sourceBranch = pullRequest.head.ref
        targetBranch = pullRequest.base.ref
        lastCommitSha = pullRequest.lastCommit()
        mergeCommitSha = pullRequest.mergeCommitSha
        authorLogin = pullRequest.user.login
        receiverLogin = pullRequest.repository.owner.login
        receiverRepositoryName = pullRequest.repository.name
        isOpened = pullRequest.state == RawGithubIssueState.OPEN
    }

}

private fun org.kohsuke.github.GHPullRequest.lastCommit(): String =
        listCommits()
                .asList()
                .lastOrNull()
                ?.sha
                ?: throw GithubException("Pull request doesn't have any commits")
