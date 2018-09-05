package org.flaxo.github

import org.flaxo.core.env.file.ByteArrayEnvironmentFile
import org.flaxo.core.env.file.EnvironmentFile
import org.flaxo.core.env.file.RemoteEnvironmentFile
import org.flaxo.git.Branch
import org.flaxo.git.Commit
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Github repository branch class.
 */
class GithubBranch(override val name: String,
                   override val repository: GithubRepository,
                   private val github: Github
) : Branch {

    private val client: RawGithub = github.client

    override fun commit(file: EnvironmentFile,
                        commitMessage: String
    ): Commit {
        val content = createContent(file, this, commitMessage)
        return GithubCommit(content.commit.shA1, this, this.github)
    }

    private fun createContent(file: EnvironmentFile,
                              branch: GithubBranch,
                              commitMessage: String
    ): RawGithubContentUpdateResponse {
        val repository = client.repository(branch.repository.owner, branch.repository.name)
        return when (file) {
            is ByteArrayEnvironmentFile ->
                repository.createContent(file.binaryContent, commitMessage, file.path.toString(), branch.name)
            else -> repository.createContent(file.content, commitMessage, file.path.toString(), branch.name)
        }
    }

    override fun update(file: EnvironmentFile,
                        commitMessage: String
    ): Commit = let { branch ->
        updateContent(file, branch, commitMessage).let {
            GithubCommit(it.commit.shA1, branch, branch.github)
        }
    }

    private fun updateContent(file: EnvironmentFile,
                              branch: GithubBranch,
                              commitMessage: String
    ): RawGithubContentUpdateResponse =
            client.repository(repository.owner, repository.name)
                    .getFileContent(file.path.toString(), branch.name)
                    .let {
                        when (file) {
                            is ByteArrayEnvironmentFile -> it.update(file.binaryContent, commitMessage, branch.name)
                            else -> it.update(file.content, commitMessage, branch.name)
                        }
                    }

    override fun createSubBranch(subBranchName: String): Branch {
        val rootBranchName = name

        client.repository(repository.owner, repository.name).apply {
            getBranch(rootBranchName).shA1.also {
                createBranch(subBranchName, it)
            }
        }

        return GithubBranch(subBranchName, repository, github)
    }

    override fun createSubBranches(count: Int, prefix: String) {
        (1..count).map { prefix + it }
                .forEach { createSubBranch(it) }
    }

    override fun files(): List<EnvironmentFile> =
            client.repository(repository.owner, repository.name)
                    .getTreeRecursive(name, 1)
                    .tree
                    .filter { it.type == "blob" }
                    .map { RemoteEnvironmentFile(Paths.get(it.path), it.readAsBlob()) }

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