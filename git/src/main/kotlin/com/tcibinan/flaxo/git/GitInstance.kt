package com.tcibinan.flaxo.git

interface GitInstance : Git {
    fun createRepository(repositoryName: String): RepositoryInstance
    fun createBranch(repository: Repository, branchName: String): BranchInstance
}