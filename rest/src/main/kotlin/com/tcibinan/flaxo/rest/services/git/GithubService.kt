package com.tcibinan.flaxo.rest.services.git

import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.GitInstance

class GithubService : GitService {
    override fun with(credentials: String): GitInstance {
        TODO("not implemented")
    }

    override fun branches(userName: String, repositoryName: String): List<Branch> {
        TODO("not implemented")
    }

}