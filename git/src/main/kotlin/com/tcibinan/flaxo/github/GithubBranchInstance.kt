package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.BranchInstance
import com.tcibinan.flaxo.git.RepositoryInstance

class GithubBranchInstance(
        private val name: String,
        private val repositoryInstance: RepositoryInstance
) : BranchInstance, Branch by GithubBranch(name, repositoryInstance) {

    override fun load(path: String, content: String): BranchInstance {
        TODO("not implemented")
    }

    override fun createSubBranches(count: Int): BranchInstance {
        TODO("not implemented")
    }

}