package com.tcibinan.flaxo.travis.build

import com.tcibinan.flaxo.travis.webhook.TravisWebHook

class SimpleTravisPullRequestBuild(webHook: TravisWebHook) : TravisPullRequestBuild {

    private val payload = webHook.payload

    override val status: BuildStatus =
            when (payload.status_message) {
                "Pending" -> BuildStatus.IN_PROGRESS
                in setOf("Passed", "Fixed") -> BuildStatus.SUCCEED
                in setOf("Broken", "Failed", "Canceled", "Errored", "Still Failing") -> BuildStatus.FAILED
                else -> BuildStatus.UNSUPPORTED
            }

    override val repositoryOwner: String = payload.repository.owner_name

    override val repositoryName: String = payload.repository.name

    override val author: String = payload.author_name

    override val branch: String = payload.branch

}

