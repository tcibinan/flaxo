package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.Repository

class GithubBranch(
        private val name: String,
        private val repository: Repository
) : Branch {
    override fun name() = name
    override fun repository() = repository
}