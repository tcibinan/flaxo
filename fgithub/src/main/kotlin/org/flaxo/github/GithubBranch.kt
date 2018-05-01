package org.flaxo.github

import org.flaxo.core.env.BinaryEnvironmentFile
import org.flaxo.core.env.EnvironmentFile
import org.flaxo.git.Branch
import org.flaxo.git.Git
import org.flaxo.git.Repository

/**
 * Github repository branch class.
 */
data class GithubBranch(override val name: String,
                        override val repository: Repository,
                        private val git: Git
) : Branch {

    override fun load(file: EnvironmentFile): Branch =
            load(file.name, file)

    override fun load(filePath: String,
                      file: EnvironmentFile
    ): Branch = also { branch ->
        when (file) {
            is BinaryEnvironmentFile
            -> git.load(repository, branch, filePath, file.binaryContent())
            else -> git.load(repository, branch, filePath, file.content())
        }
    }

    override fun createSubBranches(count: Int, prefix: String): Branch = also {
        (1..count).map { prefix + it }
                .forEach { git.createSubBranch(repository, this, it) }
    }

    override fun files(): List<EnvironmentFile> =
            git.files(repository.owner, repository.name, name)

}