package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.Repository

data class GithubRepository(
                            private val name: String,
                            private val owner: String,
                            private val github: Github
) : Repository {
    override fun name() = name
    override fun owner() = owner

    override fun createBranch(branchName: String): Branch {
        github.createBranch(this, branchName)
        return GithubBranch(branchName, this, github)
    }

}