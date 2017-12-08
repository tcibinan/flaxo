package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.Repository

internal class GithubRepository(val repositoryName: String): Repository {
    override fun checkoutBranch(baseBranch: String, branch: String) {
        TODO("not implemented")
    }

    override fun createBranch(branch: String): Branch {
        TODO("not implemented")
    }

    override fun create(): Repository {
        TODO("not implemented")
    }

    override fun delete(): Repository {
        TODO("not implemented")
    }

}