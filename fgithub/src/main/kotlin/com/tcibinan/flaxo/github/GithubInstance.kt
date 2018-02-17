package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.BranchInstance
import com.tcibinan.flaxo.git.GitInstance
import com.tcibinan.flaxo.git.Repository
import com.tcibinan.flaxo.git.RepositoryInstance
import org.kohsuke.github.GHEvent
import java.net.URL
import org.kohsuke.github.GitHub as KohsukeGit

class GithubInstance(
        private val credentials: String,
        rawWebHookUrl: String,
        private val webHookUrl: URL = URL(rawWebHookUrl)
) : GitInstance {

    private val github: KohsukeGit by lazy { KohsukeGit.connectUsingOAuth(credentials) }

    override fun createRepository(repositoryName: String, private: Boolean): RepositoryInstance {
        val repository = github.createRepository(repositoryName).create()
        repository.createContent(
                "# $repositoryName",
                "Initial commit from flaxo with love",
                "README.md"
        )

        return GithubRepositoryInstance(repository.id.toString(), repositoryName, nickname(), this)
    }

    override fun deleteRepository(repositoryName: String) {
        ghRepository(repositoryName).delete()
    }

    override fun createBranch(repository: Repository, branchName: String): BranchInstance {
        val ghRepository = ghRepository(repository.name())
        val lastCommitSha = ghRepository.listCommits().asList().last().shA1

        ghRepository.createBranch(branchName, lastCommitSha)
        return GithubBranchInstance(
                branchName,
                GithubRepositoryInstance(repository.id(), repository.name(), repository.owner(), this)
        )
    }

    override fun createSubBranch(repository: Repository, branch: Branch, subBranchName: String) {
        val ghRepository = ghRepository(repository.name())
        val sourceBranchSha = ghRepository.getBranch(branch.name()).shA1

        ghRepository.createBranch(subBranchName, sourceBranchSha)
    }

    override fun load(repository: Repository, branch: Branch, path: String, content: String) {
        repository.loadFile(content, "feat: Add $path", path, branch.name())
    }

    override fun branches(user: String, repository: String): List<Branch> =
            github.getUser(user).getRepository(repository).branchesList()

    override fun addWebHook(repositoryName: String) {
        ghRepository(repositoryName)
                .createWebHook(webHookUrl, listOf(GHEvent.PULL_REQUEST))
    }

    override fun nickname() = github.myself.login

    private fun repositoryRef(repositoryName: String) = "${nickname()}/$repositoryName"

    private fun ghRepository(repositoryName: String) = github.getRepository(repositoryRef(repositoryName))

    private fun Repository.loadFile(content: String, message: String, path: String, name: String) {
        ghRepository(name()).createContent(content, message, path, name)
    }
}