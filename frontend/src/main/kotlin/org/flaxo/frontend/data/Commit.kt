package org.flaxo.frontend.data

import kotlin.js.Date

class Commit(val sha: String,
             val pullRequestId: Int?,
             val date: Date?)