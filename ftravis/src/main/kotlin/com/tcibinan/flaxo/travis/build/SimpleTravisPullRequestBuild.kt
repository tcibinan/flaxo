package com.tcibinan.flaxo.travis.build

import com.tcibinan.flaxo.travis.webhook.TravisWebHook

class SimpleTravisPullRequestBuild(webHook: TravisWebHook) : TravisPullRequestBuild {

    override val status: BuildStatus =
            when (webHook.status_message) {
                "Pending" -> BuildStatus.IN_PROGRESS
                in setOf("Passed", "Fixed") -> BuildStatus.SUCCEED
                in setOf("Broken", "Failed", "Canceled", "Errored", "Still Failing") -> BuildStatus.FAILED
                else -> BuildStatus.UNSUPPORTED
            }

    override val repositoryOwner: String = webHook.repository.owner_name

    override val repositoryName: String = webHook.repository.name

    override val author: String = webHook.author_name

    override val branch: String = webHook.branch

}

