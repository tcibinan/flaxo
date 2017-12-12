package com.tcibinan.flaxo.rest.services

import com.tcibinan.flaxo.git.GitInstance

interface GitService {
    fun with(credentials: String): GitInstance
}