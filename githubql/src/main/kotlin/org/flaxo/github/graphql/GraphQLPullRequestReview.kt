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
        fun from(rawReview: ReviewsQuery.Node): GraphQLPullRequestReview? {
            val status: PullRequestReviewStatus = PullRequestReviewStatus.valueOf(rawReview.state.name)
            val user: String = rawReview.author?.login
                    ?: return null
            val body: String? = rawReview.body
            val submittedDate: LocalDateTime = rawReview.submittedAt?.toLocalDateTime() ?: LocalDateTime.MIN
            val commitId: String = rawReview.commit?.id
                    ?: return null

            return GraphQLPullRequestReview(
                    status = status,
                    user = user,
                    body = body,
                    submittedDate = submittedDate,
                    commitId = commitId
            )
        }
    }
}
