package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.Git
import org.kohsuke.github.GitHub as KohsukeGit

class Github : Git {
    private val gitHub: KohsukeGit by lazy { KohsukeGit.connectAnonymously() }

    override fun branches(user: String, repository: String): List<Branch> =
            gitHub.getUser(user)
                    .getRepository(repository)
                    .branches.values
                    .map { GithubBranch(it.name, GithubRepository(it.owner.name, it.owner.ownerName)) }
}