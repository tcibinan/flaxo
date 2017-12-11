package com.tcibinan.flaxo.rest.services.git

import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.GitInstance

interface GitService {
    fun with(credentials: String): GitInstance
    fun branches(userName: String, repositoryName: String): List<Branch>
}