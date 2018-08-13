package org.flaxo.frontend.data

import kotlin.js.Date

class Task(val branch: String,
           val deadline: Date?,
           val url: String,
           val plagiarismReports: List<PlagiarismReport>,
           val solutions: List<Solution>)