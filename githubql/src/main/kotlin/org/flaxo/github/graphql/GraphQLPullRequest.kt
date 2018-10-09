package org.flaxo.github.graphql

import org.flaxo.git.PullRequest
import org.flaxo.github.graphql.type.PullRequestState

internal class GraphQLPullRequest private constructor(
        override val id: Int,
        override val lastCommitSha: String,
        override val mergeCommitSha: String?,
        override val baseBranch: String,
        override val isOpened: Boolean,
        override val authorId: String,
        override val receiverId: String,
        override val receiverRepositoryName: String
) : PullRequest {

    companion object {
        fun from(rawPullRequest: PullRequestsQuery.Node): GraphQLPullRequest? {
            val id = rawPullRequest.number
            val lastCommitSha = rawPullRequest.commits.nodes.orEmpty().lastOrNull()?.commit?.id
                    ?: return null
            val mergeCommitSha = rawPullRequest.mergeCommit?.id
            val baseBranch = rawPullRequest.baseRef?.name
                    ?: return null
            val isOpened = rawPullRequest.state == PullRequestState.OPEN
            val authorId = rawPullRequest.author?.login
                    ?: return null
            val receiverId = rawPullRequest.repository.owner.login
            val receiverRepositoryName = rawPullRequest.repository.name

            return GraphQLPullRequest(
                    id = id,
                    lastCommitSha = lastCommitSha,
                    mergeCommitSha = mergeCommitSha,
                    baseBranch = baseBranch,
                    isOpened = isOpened,
                    authorId = authorId,
                    receiverId = receiverId,
                    receiverRepositoryName = receiverRepositoryName
            )
        }
    }
}
