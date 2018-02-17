package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.Repository

data class GithubRepository(
        private val id: String,
        private val name: String,
        private val owner: String
) : Repository {
    override fun id() = id
    override fun name() = name
    override fun owner() = owner
}