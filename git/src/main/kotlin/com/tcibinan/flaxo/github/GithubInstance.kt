package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.BranchInstance
import com.tcibinan.flaxo.git.Git
import com.tcibinan.flaxo.git.GitInstance
import com.tcibinan.flaxo.git.Repository
import com.tcibinan.flaxo.git.RepositoryInstance
import org.kohsuke.github.GitHub as KohsukeGit

class GithubInstance(val credentials: String) : GitInstance, Git by Github() {
    private val github: KohsukeGit by lazy { KohsukeGit.connectUsingOAuth(credentials) }

    override fun createRepository(repositoryName: String): RepositoryInstance {
        github.createRepository(repositoryName).create()
        return GithubRepositoryInstance(repositoryName, github.myself.name, this)
    }

    override fun createBranch(repository: Repository, branchName: String): BranchInstance {
        TODO("not implemented")
    }
}

