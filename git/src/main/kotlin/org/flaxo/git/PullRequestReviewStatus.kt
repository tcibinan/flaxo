package org.flaxo.git

/**
 * Pull request review status.
 */
enum class PullRequestReviewStatus {
    COMMENTED,
    CHANGES_REQUESTED,
    APPROVED,
    DISMISSED,
    PENDING,
    UNKNOWN;
}
