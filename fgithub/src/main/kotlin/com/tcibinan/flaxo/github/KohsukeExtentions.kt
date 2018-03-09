package com.tcibinan.flaxo.github

import org.kohsuke.github.GHRepository as KohsukeGithubRepository

fun KohsukeGithubRepository.createBranch(branchName: String, sourceSha: String) {
    createRef("refs/heads/$branchName", sourceSha)
}