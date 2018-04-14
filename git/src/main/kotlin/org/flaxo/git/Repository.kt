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
     * Creates a branch with the given [branchName] in the repository.
     *
     * @param branchName Newly creating branch name.
     * @return Branch with the given branchName.
     */
    fun createBranch(branchName: String): Branch
}