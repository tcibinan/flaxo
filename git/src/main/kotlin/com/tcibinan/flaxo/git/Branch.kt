package com.tcibinan.flaxo.git

import com.tcibinan.flaxo.core.env.EnvironmentFile

interface Branch {
    fun name(): String
    fun repository(): Repository
    fun files(): List<EnvironmentFile>
    fun load(file: EnvironmentFile): Branch
    fun createSubBranches(count: Int, prefix: String): Branch
}