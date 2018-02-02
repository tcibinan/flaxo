package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.Branch
import org.kohsuke.github.GHRepository

fun GHRepository.branchesList(): List<Branch> =
        branches.values.map { GithubBranch(it.name, GithubRepository(it.owner.name, it.owner.ownerName)) }

fun GHRepository.createBranch(branchName: String, sourceSha: String) {
    createRef("refs/heads/$branchName", sourceSha)
}