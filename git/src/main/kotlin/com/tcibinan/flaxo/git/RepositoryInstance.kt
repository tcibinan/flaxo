package com.tcibinan.flaxo.git

interface RepositoryInstance : Repository {
    fun createBranch(branchName: String): BranchInstance
    fun git(): GitInstance
}