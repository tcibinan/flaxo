package com.tcibinan.flaxo.git

interface Branch {
    fun name(): String
    fun repository(): Repository
}

