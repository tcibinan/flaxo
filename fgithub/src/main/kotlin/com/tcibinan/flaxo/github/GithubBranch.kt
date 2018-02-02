package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.Repository

data class GithubBranch(
        private val name: String,
        private val repository: Repository
) : Branch {
    override fun name() = name
    override fun repository() = repository

    override fun files(): Map<String, String> {
        TODO("not implemented")
    }
}