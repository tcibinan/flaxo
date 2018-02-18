package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.core.env.BinaryEnvironmentFile
import com.tcibinan.flaxo.core.env.EnvironmentFile
import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.BranchInstance
import com.tcibinan.flaxo.git.RepositoryInstance

data class GithubBranchInstance(
        private val name: String,
        private val repositoryInstance: RepositoryInstance
) : BranchInstance, Branch by GithubBranch(name, repositoryInstance) {

    override fun load(file: EnvironmentFile): BranchInstance {
        repositoryInstance.git()
                .apply {
                    when (file) {
                        is BinaryEnvironmentFile -> load(
                                repositoryInstance,
                                this@GithubBranchInstance,
                                file.name(),
                                file.binaryContent()
                        )
                        else -> load(
                                repositoryInstance,
                                this@GithubBranchInstance,
                                file.name(),
                                file.content()
                        )
                    }
                }
        return this
    }

    override fun createSubBranches(count: Int, prefix: String): BranchInstance {
        (1..count).map { prefix + it }
                .forEach { repositoryInstance.git().createSubBranch(repositoryInstance, this, it) }
        return this
    }

}