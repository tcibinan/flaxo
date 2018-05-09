package org.flaxo.github

import org.kohsuke.github.GitHub as KohsukeGithub
import org.kohsuke.github.GHRepository as KohsukeGithubRepository

fun KohsukeGithubRepository.createBranch(branchName: String, sourceSha: String) {
    createRef("refs/heads/$branchName", sourceSha)
}

fun KohsukeGithub.nickname() =
        myself.login
                ?: throw GithubException("Associated user nickname not found for the current github client")

fun KohsukeGithub.repository(repositoryName: String)
        : KohsukeGithubRepository =
        getRepository("${nickname()}/$repositoryName")

