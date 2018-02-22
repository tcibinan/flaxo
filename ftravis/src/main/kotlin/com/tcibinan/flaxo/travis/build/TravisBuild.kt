package com.tcibinan.flaxo.travis.build

interface TravisBuild {
    val status: BuildStatus
    val author: String
    val branch: String
}

