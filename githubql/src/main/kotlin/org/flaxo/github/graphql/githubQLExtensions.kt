package org.flaxo.github.graphql

import org.flaxo.git.PullRequestReviewStatus
import org.flaxo.github.graphql.type.AddPullRequestReviewInput
import org.flaxo.github.graphql.type.PullRequestReviewEvent
import org.flaxo.github.graphql.type.PullRequestReviewState

internal fun AddReviewRequest.toInput(): AddPullRequestReviewInput {
    if (state == PullRequestReviewStatus.CHANGES_REQUESTED && body == null) {
        throw GithubQLException("Pull request body is required while adding changes requested review")
    }
    return AddPullRequestReviewInput.builder().apply {
        body(body)
        event(state.toEvent())
        pullRequestId(pullRequestId)
    }.build()
}

internal fun PullRequestReviewStatus.toEvent(): PullRequestReviewEvent =
        when (this) {
            PullRequestReviewStatus.APPROVED -> PullRequestReviewEvent.APPROVE
            PullRequestReviewStatus.CHANGES_REQUESTED -> PullRequestReviewEvent.REQUEST_CHANGES
            PullRequestReviewStatus.COMMENTED -> PullRequestReviewEvent.COMMENT
            else -> throw GithubQLException("Pull request review status $this cannot be use as pull request " +
                    "review event")
        }

internal fun PullRequestReviewState.toStatus(): PullRequestReviewStatus =
        when (this) {
            PullRequestReviewState.APPROVED -> PullRequestReviewStatus.APPROVED
            PullRequestReviewState.CHANGES_REQUESTED -> PullRequestReviewStatus.CHANGES_REQUESTED
            PullRequestReviewState.COMMENTED -> PullRequestReviewStatus.COMMENTED
            PullRequestReviewState.DISMISSED -> PullRequestReviewStatus.DISMISSED
            PullRequestReviewState.PENDING -> PullRequestReviewStatus.PENDING
            PullRequestReviewState.`$UNKNOWN` -> PullRequestReviewStatus.UNKNOWN
        }
