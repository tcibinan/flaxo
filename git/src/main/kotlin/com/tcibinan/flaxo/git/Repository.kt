package com.tcibinan.flaxo.git

interface Repository {
    fun name(): String
    fun owner(): String
}