package org.flaxo.codacy.model

/**
 * Commit details data class.
 */
data class CommitDetails(val nrIssues: Int = 0,
                         val grade: String = "",
                         val complexity: Int = 0,
                         val coverage: Int = 0,
                         val nrClones: Int = 0
)