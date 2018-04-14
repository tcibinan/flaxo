package org.flaxo.travis.build

import org.flaxo.travis.webhook.TravisWebHook

/**
 * Travis pull request build implementation class.
 */
class SimpleTravisPullRequestBuild(webHook: TravisWebHook)
    : TravisPullRequestBuild {

    override val status: BuildStatus =
            when (webHook.status_message) {
                "Pending" -> BuildStatus.IN_PROGRESS
                in setOf("Passed", "Fixed") -> BuildStatus.SUCCEED
                in setOf("Broken", "Failed", "Canceled", "Errored", "Still Failing") -> BuildStatus.FAILED
                else -> BuildStatus.UNSUPPORTED
            }

    override val repositoryOwner: String = webHook.repository.owner_name

    override val repositoryName: String = webHook.repository.name

    override val number: Int = webHook.pull_request_number?.toInt()
            ?: throw Exception("Pull request number could not be null for travis pull request web hook.")

    override val branch: String = webHook.branch

}

