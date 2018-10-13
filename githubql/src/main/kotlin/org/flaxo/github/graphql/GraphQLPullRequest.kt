package org.flaxo.github.graphql

import org.flaxo.git.PullRequest
import org.flaxo.github.graphql.type.PullRequestState

internal data class GraphQLPullRequest constructor(
        override val id: String,
        override val number: Int,
        override val lastCommitSha: String,
        override val mergeCommitSha: String?,
        override val sourceBranch: String,
        override val targetBranch: String,
        override val isOpened: Boolean,
        override val authorId: String,
        override val authorLogin: String,
        override val receiverId: String,
        override val receiverLogin: String,
        override val receiverRepositoryId: String,
        override val receiverRepositoryName: String
) : PullRequest {

    companion object {
        fun from(rawPullRequest: PullRequestsQuery.Node): GraphQLPullRequest? {
            val id = rawPullRequest.id
            val number = rawPullRequest.number
            val lastCommitSha = rawPullRequest.commits.nodes.orEmpty().lastOrNull()?.commit?.oid
                    ?: return null
            val mergeCommitSha = rawPullRequest.potentialMergeCommit?.oid
            val sourceBranch = rawPullRequest.headRef?.name
                    ?: return null
            val targetBranch = rawPullRequest.baseRef?.name
                    ?: return null
            val isOpened = rawPullRequest.state == PullRequestState.OPEN
            val authorId = rawPullRequest.headRef.repository.owner.id
            val authorLogin = rawPullRequest.author?.login
                    ?: return null
            val receiverId = rawPullRequest.repository.owner.id
            val receiverLogin = rawPullRequest.repository.owner.login
            val receiverRepositoryId = rawPullRequest.repository.id
            val receiverRepositoryName = rawPullRequest.repository.name

            return GraphQLPullRequest(
                    id = id,
                    number = number,
                    lastCommitSha = lastCommitSha,
                    mergeCommitSha = mergeCommitSha,
                    sourceBranch = sourceBranch,
                    targetBranch = targetBranch,
                    isOpened = isOpened,
                    authorId = authorId,
                    authorLogin = authorLogin,
                    receiverId = receiverId,
                    receiverLogin = receiverLogin,
                    receiverRepositoryId = receiverRepositoryId,
                    receiverRepositoryName = receiverRepositoryName
            )
        }
    }
}
