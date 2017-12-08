package com.tcibinan.flaxo.git

interface Repository {
    fun create(): Repository
    fun delete(): Repository
    fun createBranch(branch: String): Branch
    fun checkoutBranch(baseBranch: String, branch: String)
}