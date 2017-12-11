package com.tcibinan.flaxo.git

interface Git {
    fun branches(user: String, repository: String): List<Branch>
}