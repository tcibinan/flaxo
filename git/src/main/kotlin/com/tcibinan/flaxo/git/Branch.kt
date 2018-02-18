package com.tcibinan.flaxo.git

import com.tcibinan.flaxo.core.env.EnvironmentFile

interface Branch {
    fun name(): String
    fun repository(): Repository
    fun files(): Set<EnvironmentFile>
}