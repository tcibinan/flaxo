package org.flaxo.github

import org.flaxo.core.env.BinaryEnvironmentFile
import org.flaxo.core.env.EnvironmentFile
import org.flaxo.core.env.RemoteEnvironmentFile
import org.flaxo.git.Branch
import org.flaxo.git.Commit
import org.kohsuke.github.GHContentUpdateResponse
import org.kohsuke.github.GitHub as KohsukeGithub

/**
 * Github repository branch class.
 */
class GithubBranch(override val name: String,
                   override val repository: GithubRepository,
                   private val github: Github
) : Branch {

    private val client: KohsukeGithub = github.client

    override fun commit(file: EnvironmentFile,
                        filePath: String,
                        commitMessage: String
    ): Commit = let { branch ->
        createContent(file, filePath, branch, commitMessage).let {
            GithubCommit(it.commit.shA1, branch, branch.github)
        }
    }

    private fun createContent(file: EnvironmentFile,
                              filePath: String,
                              branch: GithubBranch,
                              commitMessage: String
    ): GHContentUpdateResponse =
            client.repository(branch.repository.name)
                    .let {
                        when (file) {
                            is BinaryEnvironmentFile ->
                                it.createContent(file.binaryContent(), commitMessage, filePath, branch.name)
                            else -> it.createContent(file.content(), commitMessage, filePath, branch.name)
                        }
                    }

    override fun update(file: EnvironmentFile,
                        filePath: String,
                        commitMessage: String
    ): Commit = let { branch ->
        updateContent(file, filePath, branch, commitMessage).let {
            GithubCommit(it.commit.shA1, branch, branch.github)
        }
    }

    private fun updateContent(file: EnvironmentFile,
                              filePath: String,
                              branch: GithubBranch,
                              commitMessage: String
    ): GHContentUpdateResponse =
            client.repository(repository.name)
                    .getFileContent(filePath, branch.name)
                    .let {
                        when (file) {
                            is BinaryEnvironmentFile -> it.update(file.binaryContent(), commitMessage, branch.name)
                            else -> it.update(file.content(), commitMessage, branch.name)
                        }
                    }

    override fun createSubBranch(subBranchName: String): Branch {
        val rootBranchName = name

        client.repository(repository.name).apply {
            getBranch(rootBranchName).shA1.also {
                createBranch(subBranchName, it)
            }
        }

        return GithubBranch(subBranchName, repository, github)
    }

    override fun createSubBranches(count: Int,
                                   prefix: String
    ) {
        (1..count).map { prefix + it }
                .forEach { createSubBranch(it) }
    }

    override fun files(): List<EnvironmentFile> =
            client.repository(repository.name)
                    .getTreeRecursive(name, 1)
                    .tree
                    .filter { it.type == "blob" }
                    .map { RemoteEnvironmentFile(it.path, it.readAsBlob()) }

    override fun createPullRequestTo(targetBranch: Branch) {
        client.getUser(targetBranch.repository.owner)
                .getRepository(targetBranch.repository.name)
                .createPullRequest(
                        "$name implementation from ${client.nickname()}",
                        "${client.nickname()}:$name",
                        targetBranch.name,
                        ""
                )
    }

}