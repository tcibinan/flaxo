package com.tcibinan.flaxo.rest.service.git

import com.tcibinan.flaxo.git.GitInstance

interface GitService {
    fun with(credentials: String): GitInstance
}