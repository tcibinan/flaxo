package org.flaxo.github

internal fun RawGithub.nickname(): String = myself.login
        ?: throw GithubException("Associated github user nickname not found for the current client")

internal fun RawGithub.repository(owner: String, repositoryName: String): RawGithubRepository =
        getUser(owner).getRepository(repositoryName)

internal fun RawGithubRepository.createBranch(branchName: String, sourceSha: String) {
    createRef("refs/heads/$branchName", sourceSha)
}
