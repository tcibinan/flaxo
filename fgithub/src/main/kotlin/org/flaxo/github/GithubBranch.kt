package org.flaxo.github

import org.apache.logging.log4j.LogManager
import org.flaxo.common.env.file.EnvironmentFile
import org.flaxo.common.env.file.LazyEnvironmentFile
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

    companion object {
        private val logger = LogManager.getLogger(GithubBranch::class.java)
    }

    val client: RawGithub by lazy { github.client }
    val rawRepository: RawGithubRepository by lazy { client.repository(repository.owner, repository.name) }

    override fun commit(file: EnvironmentFile, commitMessage: String): Commit {
        val content = createContent(file, commitMessage)
        return GithubCommit(content.commit.shA1, this, this.github)
    }

    private fun createContent(file: EnvironmentFile, commitMessage: String): RawGithubContentUpdateResponse =
            rawRepository.createContent(file.binaryContent, commitMessage, file.path.toString(), name)

    override fun update(file: EnvironmentFile, commitMessage: String): Commit {
        val content = updateContent(file, commitMessage)
        return GithubCommit(content.commit.shA1, this, github)
    }

    private fun updateContent(file: EnvironmentFile, commitMessage: String): RawGithubContentUpdateResponse =
            rawRepository.getFileContent(file.path.toString(), name)
                    .update(file.binaryContent, commitMessage, name)

    override fun createSubBranch(subBranchName: String): Branch {
        val shA1 = rawRepository.getBranch(name).shA1
        rawRepository.createBranch(subBranchName, shA1)
        return GithubBranch(subBranchName, repository, github)
    }

    override fun createSubBranches(count: Int, prefix: String) {
        (1..count).map { prefix + it }
                .forEach { createSubBranch(it) }
    }

    override fun files(): List<EnvironmentFile> =
            rawRepository.getTreeRecursive(name, 1)
                    .tree
                    .filter { it.type == "blob" }
                    .mapNotNull { entry -> path(entry.path)?.let { LazyEnvironmentFile(it) { entry.readAsBlob() } } }

    private fun path(path: String): Path? =
            try {
                Paths.get(path)
            } catch (e: Exception) {
                logger.error("Invalid file system path $path. It will be skipped", e)
                null
            }

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
