package org.flaxo.git

/**
 * Git commit interface.
 */
interface Commit {

    /**
     * Sha of the current commit.
     */
    val sha: String

    /**
     * Branch the commit relates to.
     */
    val branch: Branch
}
