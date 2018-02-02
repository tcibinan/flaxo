package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.BranchInstance
import com.tcibinan.flaxo.git.RepositoryInstance

data class GithubBranchInstance(
        private val name: String,
        private val repositoryInstance: RepositoryInstance
) : BranchInstance, Branch by GithubBranch(name, repositoryInstance) {

    override fun load(path: String, content: String): BranchInstance {
        repositoryInstance.git()
                .load(repositoryInstance, this, path, content)
        return this
    }

    override fun createSubBranches(count: Int, prefix: String): BranchInstance {
        (1..count).map { prefix + it }
                .forEach { repositoryInstance.git().createSubBranch(repositoryInstance, this, it) }
        return this
    }

}