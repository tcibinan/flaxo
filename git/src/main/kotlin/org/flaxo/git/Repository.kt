package org.flaxo.git

/**
 * Git repository interface.
 */
interface Repository {

    /**
     * Git repository name.
     */
    val name: String

    /**
     * Git repository owner nickname.
     */
    val owner: String

    /**
     * Number of forks.
     */
    val forks: Int

    /**
     * Completely deletes a repository from the git service.
     */
    fun delete()

    /**
     * Creates a branch with the given [branchName] in the repository.
     *
     * @param branchName Newly creating branch name.
     * @return Branch with the given branchName.
     */
    fun createBranch(branchName: String): Branch

    /**
     * Adds web hook for the repository.
     */
    fun addWebHook()

    /**
     * Returns a list of branches of the repository.
     *
     * @return List of branches in the repository.
     */
    fun branches(): List<Branch>

    /**
     * Checks if the repository exists.
     */
    fun exists(): Boolean

    /**
     * Retrieves pull request by the given [pullRequestNumber].
     */
    fun getPullRequest(pullRequestNumber: Int): PullRequest

    /**
     * Retrieves open pull requests for this repository.
     */
    fun getOpenPullRequests(): List<PullRequest>

    /**
     * Retrieves all pull request for this repository.
     */
    fun getPullRequests(): List<PullRequest>
}
