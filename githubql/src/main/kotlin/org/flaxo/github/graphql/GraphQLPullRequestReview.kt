package org.flaxo.github.graphql

import org.flaxo.git.PullRequestReview
import org.flaxo.git.PullRequestReviewStatus
import java.time.LocalDateTime

internal class GraphQLPullRequestReview private constructor(
        override val status: PullRequestReviewStatus,
        override val user: String,
        override val body: String?,
        override val submittedDate: LocalDateTime,
        override val commitId: String
) : PullRequestReview {

    companion object {
        fun from(rawReview: ReviewsQuery.Node): GraphQLPullRequestReview? =
                from(
                        status = rawReview.state.toStatus(),
                        user = rawReview.author?.login,
                        body = rawReview.body,
                        submittedDate = rawReview.submittedAt?.toLocalDateTime(),
                        commitId = rawReview.commit?.id
                )

        fun from(rawReview: AddReviewMutation.PullRequestReview): GraphQLPullRequestReview? =
                from(
                        status = rawReview.state.toStatus(),
                        user = rawReview.author?.login,
                        body = rawReview.body,
                        submittedDate = rawReview.submittedAt?.toLocalDateTime(),
                        commitId = rawReview.commit?.id
                )

        fun from(status: PullRequestReviewStatus,
                 user: String?,
                 body: String?,
                 submittedDate: LocalDateTime?,
                 commitId: String?
        ): GraphQLPullRequestReview? {
            return GraphQLPullRequestReview(
                    status = status,
                    user = user ?: return null,
                    body = body,
                    submittedDate = submittedDate ?: LocalDateTime.MIN,
                    commitId = commitId ?: return null
            )
        }
    }
}
