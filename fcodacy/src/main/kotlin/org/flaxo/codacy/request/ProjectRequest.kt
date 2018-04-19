package org.flaxo.codacy.request

/**
 * Data class for project creation request.
 */
data class ProjectRequest(val name: String = "",
                          val url: String = ""
)