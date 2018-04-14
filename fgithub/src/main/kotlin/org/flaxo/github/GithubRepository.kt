package org.flaxo.github

import org.flaxo.git.Branch
import org.flaxo.git.Git
import org.flaxo.git.Repository

data class GithubRepository(override val name: String,
                            override val owner: String,
                            private val git: Git
) : Repository {

    override fun createBranch(branchName: String): Branch =
            git.createBranch(this, branchName)

}