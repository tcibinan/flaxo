package com.tcibinan.flaxo.git

interface Repository {
    fun id(): String
    fun name(): String
    fun owner(): String
}