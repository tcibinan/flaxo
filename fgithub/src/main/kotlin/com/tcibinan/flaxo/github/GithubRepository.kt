package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.Git
import com.tcibinan.flaxo.git.Repository

data class GithubRepository(private val name: String,
                            private val owner: String,
                            private val git: Git
) : Repository {
    override fun name() = name
    override fun owner() = owner

    override fun createBranch(branchName: String): Branch {
        git.createBranch(this, branchName)
        return GithubBranch(branchName, this, git)
    }

}