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
     * Commit and push the given file.
     *
     * @return the current branch.
     */
    fun load(file: EnvironmentFile): Branch

    /**
     * Commit and push the given file by given path in repository.
     *
     * @return the current branch.
     */
    fun load(filePath: String,
             file: EnvironmentFile
    ): Branch

    /**
     * Creates several branches which start from the current branch.
     *
     * Each branch have name with [prefix] and an order number.
     *
     * @return the current branch.
     */
    fun createSubBranches(count: Int,
                          prefix: String
    ): Branch
}