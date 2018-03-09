package com.tcibinan.flaxo.git

/**
 * Git pull request interface.
 */
interface PullRequest : GitPayload {

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