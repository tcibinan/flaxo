package com.tcibinan.flaxo.git

interface BranchInstance : Branch {
    fun load(path: String, content: String): BranchInstance
    fun createSubBranches(count: Int): BranchInstance
}
