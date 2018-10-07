package org.flaxo.github.graphql

import org.flaxo.git.PullRequestReview
import org.flaxo.git.PullRequestReviewStatus
import java.time.LocalDateTime

internal class GraphQLPullRequestReview(rawReview: ReviewsQuery.Node) : PullRequestReview {
    override val status: PullRequestReviewStatus = PullRequestReviewStatus.valueOf(rawReview.state.name)
    override val user: String = rawReview.author?.login!!
    override val body: String? = rawReview.body
    override val submittedDate: LocalDateTime = rawReview.submittedAt?.toLocalDateTime() ?: LocalDateTime.MIN
    override val commitId: String = rawReview.commit?.id!!
}
