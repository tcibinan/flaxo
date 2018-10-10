package org.flaxo.github.graphql

import org.flaxo.git.PullRequestReviewStatus

/**
 * Add pull request review request.
 */
class AddReviewRequest(val pullRequestId: String,
                       val body: String?,
                       val state: PullRequestReviewStatus
)