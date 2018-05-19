package org.flaxo.git

/**
 * Git pull request interface.
 */
interface PullRequest : GitPayload {

    /**
     * Newest pull request commit sha.
     */
    val lastCommitSha: String

    /**
     * Git creates a technical commit where it merges target and source branches.
     * This property represents sha of that commit.
     */
    val mergeCommitSha: String

    /**
     * Pull request base branch.
     */
    val baseBranch: String

    /**
     * Checks if the pull request state is *opened*.
     */
    val isOpened: Boolean

    /**
     * Pull request sender git nickname.
     */
    val authorId: String

    /**
     * Pull request destination repository owner nickname.
     */
    val receiverId: String

    /**
     * Pull request destination repository name.
     */
    val receiverRepositoryName: String
}