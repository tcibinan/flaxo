package org.flaxo.common

class PlagiarismReport(val url: String,
                       val date: DateTime,
                       val matches: List<PlagiarismMatch>
)