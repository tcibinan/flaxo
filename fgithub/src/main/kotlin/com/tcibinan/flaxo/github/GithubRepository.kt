package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.Git
import com.tcibinan.flaxo.git.Repository

data class GithubRepository(override val name: String,
                            override val owner: String,
                            private val git: Git
) : Repository {

    override fun createBranch(branchName: String): Branch =
            git.createBranch(this, branchName)

}