package org.flaxo.git

import org.flaxo.core.env.EnvironmentFile

/**
 * Git repository branch interface.
 */
interface Branch {

    /**
     * Name of the current branch.
     */
    val name: String

    /**
     * Repository which this branch is part of.
     */
    val repository: Repository

    /**
     * @return list of environment files of the current branch.
     */
    fun files(): List<EnvironmentFile>

    /**
     * Commit and push the given file by given path in repository.
     *
     * @return Performed commit.
     */
    fun commit(file: EnvironmentFile,
               filePath: String = file.name
    ): Commit

    /**
     * Commit and push an existing file by given path in repository.
     *
     * @return Performed commit.
     */
    fun update(file: EnvironmentFile,
               filePath: String = file.name
    ): Commit

    /**
     * Creates a branch that checkouts from the current branch.
     */
    fun createSubBranch(subBranchName: String): Branch

    /**
     * Creates several branches which start from the current branch.
     *
     * Each branch have name with [prefix] and an order number.
     *
     * @return the current branch.
     */
    fun createSubBranches(count: Int,
                          prefix: String
    )

    /**
     * Creates a pull request where base branch is current branch
     * and target branch is [targetBranch].
     */
    fun createPullRequestTo(targetBranch: Branch)
}