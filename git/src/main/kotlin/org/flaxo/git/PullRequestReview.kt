package org.flaxo.git

import java.time.LocalDateTime

/**
 * Git pull request review
 */
interface PullRequestReview: GitPayload {

    /**
     * Review status.
     */
    val status: PullRequestReviewStatus

    /**
     * Github nickname of the user that created the review.
     */
    val user: String

    /**
     * Review body.
     */
    val body: String?

    /**
     * Review submitted date.
     */
    val submittedDate: LocalDateTime

    /**
     * Pull request commit id.
     */
    val commitId: String
}
