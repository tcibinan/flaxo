package com.tcibinan.flaxo.git

import com.tcibinan.flaxo.core.env.EnvironmentFile

interface BranchInstance : Branch {
    fun load(file: EnvironmentFile): BranchInstance
    fun createSubBranches(count: Int, prefix: String): BranchInstance
}
