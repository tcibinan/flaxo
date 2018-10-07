package org.flaxo.github.graphql

import org.flaxo.git.PullRequest
import org.flaxo.github.graphql.type.PullRequestState

internal class GraphQLPullRequest(rawPullRequest: PullRequestsQuery.Node): PullRequest {
    override val id: Int = rawPullRequest.number
    override val lastCommitSha: String = rawPullRequest.commits.nodes.orEmpty().last().commit.id
    override val mergeCommitSha: String? = rawPullRequest.mergeCommit?.id
    override val baseBranch: String = rawPullRequest.baseRef?.name!!
    override val isOpened: Boolean = rawPullRequest.state == PullRequestState.OPEN
    override val authorId: String = rawPullRequest.author?.login!!
    override val receiverId: String = rawPullRequest.repository.owner.login
    override val receiverRepositoryName: String = rawPullRequest.repository.name
}
