package com.tcibinan.flaxo.github

import org.kohsuke.github.GHRepository

fun GHRepository.createBranch(branchName: String, sourceSha: String) {
    createRef("refs/heads/$branchName", sourceSha)
}