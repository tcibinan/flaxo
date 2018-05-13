package org.flaxo.travis.retrofit

import org.flaxo.travis.TravisBuildStatus
import org.flaxo.travis.TravisPullRequestBuild

class RetrofitTravisBuild(pojo: RetrofitTravisBuildPOJO): TravisPullRequestBuild {

    override val repositoryOwner: String = pojo.repository.owner.name

    override val repositoryName: String = pojo.repository.name

    override val pullRequestNumber: Int = pojo.pull_request_number.toInt()

    override val buildStatus: TravisBuildStatus = TravisBuildStatus.retrieve(pojo.state)

    override val branch: String = pojo.branch.name

}
