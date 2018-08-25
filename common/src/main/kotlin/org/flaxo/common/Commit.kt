package org.flaxo.common

class Commit(val sha: String,
             val pullRequestId: Int?,
             val date: DateTime?
)