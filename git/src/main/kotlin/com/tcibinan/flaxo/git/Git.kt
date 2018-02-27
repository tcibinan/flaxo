package com.tcibinan.flaxo.git

import com.tcibinan.flaxo.core.env.EnvironmentFile

interface Git {
    fun nickname(): String
    fun branches(user: String, repository: String): List<Branch>
    fun createRepository(repositoryName: String, private: Boolean = false): Repository
    fun createBranch(repository: Repository, branchName: String): Branch
    fun load(repository: Repository, branch: Branch, path: String, content: String)
    fun load(repository: Repository, branch: Branch, path: String, bytes: ByteArray)
    fun createSubBranch(repository: Repository, branch: Branch, subBranchName: String)
    fun deleteRepository(repositoryName: String)
    fun addWebHook(repositoryName: String)
    fun files(user: String, repository: String, branch: String): List<EnvironmentFile>
    fun getPullRequest(repository: String, pullRequestNumber: Int): PullRequest
}