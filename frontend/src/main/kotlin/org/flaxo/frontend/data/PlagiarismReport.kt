package org.flaxo.frontend.data

import kotlin.js.Date

class PlagiarismReport(val url: String,
                       val date: Date,
                       val matches: List<PlagiarismMatch>)