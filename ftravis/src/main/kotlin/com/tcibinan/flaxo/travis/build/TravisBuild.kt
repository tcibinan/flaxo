package com.tcibinan.flaxo.travis.build

interface TravisBuild {
    val status: BuildStatus
    val branch: String
}

