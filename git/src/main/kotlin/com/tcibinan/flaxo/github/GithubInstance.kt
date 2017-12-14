package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.BranchInstance
import com.tcibinan.flaxo.git.Git
import com.tcibinan.flaxo.git.GitInstance
import com.tcibinan.flaxo.git.Repository
import com.tcibinan.flaxo.git.RepositoryInstance
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub as KohsukeGit

class GithubInstance(
        private val credentials: String
) : GitInstance, Git by Github() {

    private val github: KohsukeGit by lazy { KohsukeGit.connectUsingOAuth(credentials) }

    override fun createRepository(repositoryName: String): RepositoryInstance {
        val repository = github.createRepository(repositoryName).create()
        repository.createContent("# $repositoryName", "Initial commit from flaxo with love", "README.md")

        return GithubRepositoryInstance(repositoryName, nickname(), this)
    }

    override fun deleteRepository(repositoryName: String) {
        ghRepository(repositoryName).delete()
    }

    override fun createBranch(repository: Repository, branchName: String): BranchInstance {
        val ghRepository = ghRepository(repository.name())
        val lastCommitSha = ghRepository.listCommits().asList().last().shA1

        ghRepository.createBranch(branchName, lastCommitSha)
        return GithubBranchInstance(branchName, GithubRepositoryInstance(repository.name(), repository.owner(), this))
    }

    override fun createSubBranch(repository: Repository, branch: Branch, subBranchName: String) {
        val ghRepository = ghRepository(repository.name())
        val sourceBranchSha = ghRepository.getBranch(branch.name()).shA1

        ghRepository.createBranch(subBranchName, sourceBranchSha)
    }

    override fun load(repository: Repository, branch: Branch, path: String, content: String) {
        repository.loadFile(content, "feat: Add $path", path, branch.name())
    }

    private fun nickname() = github.myself.login

    private fun repositoryRef(repositoryName: String) = "${nickname()}/$repositoryName"

    private fun ghRepository(repositoryName: String) = github.getRepository(repositoryRef(repositoryName))

    private fun Repository.loadFile(content: String, message: String, path: String, name: String) {
        ghRepository(name()).createContent(content, message, path, name)
    }
}

private fun GHRepository.createBranch(branchName: String, sourceSha: String) {
    createRef("refs/heads/$branchName", sourceSha)
}