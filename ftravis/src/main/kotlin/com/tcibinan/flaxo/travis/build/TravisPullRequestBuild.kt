package com.tcibinan.flaxo.travis.build

interface TravisPullRequestBuild : TravisBuild {
    val repositoryOwner: String
    val repositoryName: String
}