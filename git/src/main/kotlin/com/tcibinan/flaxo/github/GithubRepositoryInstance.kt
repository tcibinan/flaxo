package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.BranchInstance
import com.tcibinan.flaxo.git.Repository
import com.tcibinan.flaxo.git.RepositoryInstance

class GithubRepositoryInstance(
        private val name: String,
        private val owner: String,
        private val githubInstance: GithubInstance
) : RepositoryInstance, Repository by GithubRepository(name, owner) {
    override fun createBranch(branchName: String): BranchInstance {
        githubInstance.createBranch(this, branchName)
        return GithubBranchInstance(branchName, this)
    }

    override fun git() = githubInstance

}