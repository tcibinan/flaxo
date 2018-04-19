package org.flaxo.codacy.model

/**
 * Project file data class.
 */
data class File(val path: String = "",
                val newIssues: Int = 0,
                val fixedIssues: Int = 0
)