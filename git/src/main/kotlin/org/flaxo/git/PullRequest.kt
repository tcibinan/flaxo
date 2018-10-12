package org.flaxo.git

/**
 * Git pull request interface.
 *
 * Pull request sender is the one who created the pull request.
 * Pull request receiver is the owner of the destination repository.
 *
 * |---------------------------------------------------------------|
 * |                                                               |
 * | Sender repository                         Receiver repository |
 * |                                                               |
 * |                        pull request                           |
 * | source-branch         ------------->      target-branch       |
 * |                                                               |
 * |---------------------------------------------------------------|
 */
interface PullRequest : GitPayload {

    /**
     * Pull request id.
     *
     * Pull request primary key.
     */
    val id: String

    /**
     * Pull request number.
     *
     * It is unique within a single git repository.
     */
    val number: Int

    /**
     * Newest pull request commit sha.
     */
    val lastCommitSha: String

    /**
     * Synthetic commit that merges base and head branches.
     *
     * Git creates a technical commit where it merges target and source branches.
     * This property represents sha of that commit.
     *
     * It will not be presented if merge commit is not constructed by git yet.
     */
    val mergeCommitSha: String?

    /**
     * Pull request source branch.
     *
     * Sender repository branch that is requested to be pulled into a target branch.
     */
    val sourceBranch: String

    /**
     * Pull request target (or base) branch.
     *
     * Receiver repository branch changes are requested to be pulled into.
     */
    val targetBranch: String

    /**
     * Checks if the pull request state is *opened*.
     */
    val isOpened: Boolean

    /**
     * Pull request sender id.
     */
    val authorId: String

    /**
     * Pull request sender nickname.
     */
    val authorLogin: String

    /**
     * Pull request receiver id.
     */
    val receiverId: String

    /**
     * Pull request receiver nickname.
     */
    val receiverLogin: String

    /**
     * Pull request receiver repository id.
     */
    val receiverRepositoryId: String

    /**
     * Pull request destination repository name.
     */
    val receiverRepositoryName: String
}
