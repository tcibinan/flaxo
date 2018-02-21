package com.tcibinan.flaxo.git

interface PullRequest : GitWebHook {
    val isOpened: Boolean
}