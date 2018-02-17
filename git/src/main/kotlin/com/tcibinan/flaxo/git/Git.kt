package com.tcibinan.flaxo.git

interface Git {
    fun nickname(): String
    fun branches(user: String, repository: String): List<Branch>
}