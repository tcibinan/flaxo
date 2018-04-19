package org.flaxo.codacy.model

/**
 * Commit delta data class.
 */
data class CommitDelta(val newIssues: Int = 0,
                       val fixedIssues: Int = 0,
                       val files: List<File> = emptyList()
)
