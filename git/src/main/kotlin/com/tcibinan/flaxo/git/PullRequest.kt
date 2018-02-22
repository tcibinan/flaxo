package com.tcibinan.flaxo.git

interface PullRequest : GitPayload {
    val isOpened: Boolean
    val authorId: String
    val receiverId: String
    val receiverRepositoryName: String
}