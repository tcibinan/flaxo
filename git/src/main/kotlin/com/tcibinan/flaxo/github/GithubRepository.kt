package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.Repository

class GithubRepository(
        private val name: String,
        private val owner: String
) : Repository {
    override fun name() = name
    override fun owner() = owner
}