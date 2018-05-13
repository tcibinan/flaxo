package org.flaxo.travis.webhook

import org.flaxo.travis.TravisBuildStatus
import org.flaxo.travis.TravisPullRequestBuild

/**
 * Travis pull request build implementation class.
 */
class SimpleTravisPullRequestBuild(webHook: TravisWebHook)
    : TravisPullRequestBuild {

    override val buildStatus: TravisBuildStatus =
            TravisBuildStatus.retrieve(webHook.status_message)

    override val repositoryOwner: String = webHook.repository.owner_name

    override val repositoryName: String = webHook.repository.name

    override val pullRequestNumber: Int = webHook.pull_request_number?.toInt()
            ?: throw Exception("Pull request number could not be null for travis pull request web hook.")

    override val branch: String = webHook.branch

}
