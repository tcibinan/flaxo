package org.flaxo.common

class Task(val branch: String,
           val deadline: DateTime?,
           val url: String,
           val plagiarismReports: List<PlagiarismReport>,
           val solutions: List<Solution>
)
