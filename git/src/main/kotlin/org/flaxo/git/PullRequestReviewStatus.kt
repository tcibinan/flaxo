package org.flaxo.git

enum class PullRequestReviewStatus {
    COMMENTED,
    CHANGES_REQUESTED,
    APPROVED,
    DISMISSED,
    PENDING,
    UNKNOWN;
}
