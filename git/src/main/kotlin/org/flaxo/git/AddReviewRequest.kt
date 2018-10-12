package org.flaxo.git

/**
 * Add pull request review request.
 */
class AddReviewRequest(val pullRequestId: String,
                       val body: String?,
                       val state: PullRequestReviewStatus
)
