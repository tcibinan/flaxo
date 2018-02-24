package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.core.env.EnvironmentFile
import com.tcibinan.flaxo.core.env.RemoteEnvironmentFile
import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.Git
import com.tcibinan.flaxo.git.Repository
import org.kohsuke.github.GHEvent
import java.net.URL
import org.kohsuke.github.GitHub as KohsukeGit

class Github(
        private val credentials: String,
        rawWebHookUrl: String
) : Git {

    private val webHookUrl: URL = URL(rawWebHookUrl)
    private val github: KohsukeGit by lazy { KohsukeGit.connectUsingOAuth(credentials) }

    override fun createRepository(repositoryName: String, private: Boolean): Repository {
        val repository = github.createRepository(repositoryName).private_(private).create()
        repository.createContent(
                "# $repositoryName",
                "Initial commit from flaxo with love",
                "README.md"
        )

        return GithubRepository(repositoryName, nickname(), this)
    }

    override fun deleteRepository(repositoryName: String) {
        ghRepository(repositoryName).delete()
    }

    override fun createBranch(repository: Repository, branchName: String): Branch {
        val ghRepository = ghRepository(repository.name())
        val lastCommitSha = ghRepository.listCommits().asList().last().shA1

        ghRepository.createBranch(branchName, lastCommitSha)
        return GithubBranch(
                branchName,
                GithubRepository(repository.name(), repository.owner(), this),
                this
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

    override fun load(repository: Repository, branch: Branch, path: String, bytes: ByteArray) {
        repository.loadFile(bytes, "feat: Add $path", path, branch.name())
    }

    override fun branches(user: String, repository: String): List<Branch> =
            github.getUser(user)
                    .getRepository(repository)
                    .branches
                    .values
                    .map { branch ->
                        GithubBranch(
                                branch.name,
                                GithubRepository(branch.owner.name, branch.owner.ownerName, this@Github),
                                this@Github
                        )
                    }

    override fun files(user: String, repository: String, branch: String): List<EnvironmentFile> =
            github.getUser(user)
                    .getRepository(repository)
                    .getTreeRecursive(branch, 1)
                    .tree
                    .filter { it.type == "blob" }
                    .map { RemoteEnvironmentFile(it.path, it.readAsBlob()) }

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

    private fun Repository.loadFile(bytes: ByteArray, message: String, path: String, name: String) {
        ghRepository(name()).createContent(bytes, message, path, name)
    }
}