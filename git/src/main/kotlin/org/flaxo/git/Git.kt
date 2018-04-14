package org.flaxo.git

import org.flaxo.core.env.EnvironmentFile

/**
 * Git service provider client.
 *
 * Client is associated with a single user.
 */
interface Git {

    /**
     * @return git service user nickname.
     */
    fun nickname(): String

    /**
     * Returns a list of branches of a repository.
     *
     * @param nickname Git owner nickname.
     * @param repositoryName Git repository name.
     * @return List of branches in the [repositoryName] where owner is [nickname].
     */
    fun branches(nickname: String,
                 repositoryName: String
    ): List<Branch>

    /**
     * Creates a git repository for the current user.
     *
     * @param repositoryName Git service repository name.
     * @param private Type of the repository private/public to create *(public by default)*.
     * @return Repository of the current user with [repositoryName] and [private] type.
     */
    fun createRepository(repositoryName: String,
                         private: Boolean = false
    ): Repository

    /**
     * Creates a branch it the [repository].
     *
     * @param repository To create a branch in.
     * @param branchName Newly creating branch name.
     */
    fun createBranch(repository: Repository,
                     branchName: String
    ): Branch

    /**
     * Commits and pushes a file with the given [path] and [content].
     *
     * @param repository To push file to.
     * @param branch To push file to.
     * @param path File path with the name of the file itself.
     * @param content String file content.
     */
    fun load(repository: Repository,
             branch: Branch,
             path: String,
             content: String)

    /**
     * Commits and pushes a file with the given [path] and [bytes] content.
     *
     * @param repository To push file to.
     * @param branch To push file to.
     * @param path File path with the name of the file itself.
     * @param bytes Binary content of the file.
     */
    fun load(repository: Repository,
             branch: Branch,
             path: String,
             bytes: ByteArray)

    /**
     * Creates a branch that checkouts from the [branch] of the [repository].
     *
     * @param repository To create a subbranch in.
     * @param branch The original branch.
     * @param subBranchName Newly creating branch name.
     */
    fun createSubBranch(repository: Repository,
                        branch: Branch,
                        subBranchName: String)

    /**
     * Completely deletes a repository from the git service.
     *
     * @param repositoryName To be deleted.
     */
    fun deleteRepository(repositoryName: String)

    /**
     * Adds git service necessary web hooks.
     *
     * @param repositoryName To set web hook for.
     */
    fun addWebHook(repositoryName: String)

    /**
     * Retrieves a list of environment files of the given repository branch.
     *
     * @param nickname Git owner nickname.
     * @param repositoryName Git repository name.
     * @param branchName Git repository branch to retrieve files from.
     * @return List of environment files from the branch.
     */
    fun files(nickname: String,
              repositoryName: String,
              branchName: String
    ): List<EnvironmentFile>

    /**
     * Retrieves [repositoryName] pull request by [pullRequestNumber].
     *
     * @param repositoryName Git repository name.
     * @param pullRequestNumber Git pull request identifier.
     */
    fun getPullRequest(repositoryName: String,
                       pullRequestNumber: Int
    ): PullRequest
}