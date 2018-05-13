package org.flaxo.travis

/**
 * Travis pull request build interface.
 */
interface TravisPullRequestBuild : TravisBuild {

    /**
     * Git repository owner nickname.
     *
     * Repository pull request is directed to.
     */
    val repositoryOwner: String

    /**
     * Git repository name.
     *
     * Repository pull request is directed to.
     */
    val repositoryName: String

    /**
     * Git pull request identifier.
     */
    val pullRequestNumber: Int
}