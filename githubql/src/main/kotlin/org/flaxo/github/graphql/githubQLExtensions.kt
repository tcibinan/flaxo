package org.flaxo.github.graphql

import org.flaxo.git.PullRequestReviewStatus
import org.flaxo.github.graphql.type.AddPullRequestReviewInput
import org.flaxo.github.graphql.type.PullRequestReviewEvent

internal fun AddReviewRequest.toInput(): AddPullRequestReviewInput {
    if (state == PullRequestReviewStatus.CHANGES_REQUESTED && body == null) {
        throw GithubQLException("Pull request body is required while adding changes requested review")
    }
    return AddPullRequestReviewInput.builder().apply {
        body(body)
        event(when (state) {
            PullRequestReviewStatus.APPROVED -> PullRequestReviewEvent.APPROVE
            PullRequestReviewStatus.CHANGES_REQUESTED -> PullRequestReviewEvent.REQUEST_CHANGES
            PullRequestReviewStatus.COMMENTED -> PullRequestReviewEvent.COMMENT
            else -> throw GithubQLException("Pull request state $state is not support while adding review")
        })
        pullRequestId(pullRequestId)
    }.build()
}
