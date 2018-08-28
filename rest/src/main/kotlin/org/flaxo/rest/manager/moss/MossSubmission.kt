package org.flaxo.rest.manager.moss

import org.flaxo.core.env.file.LocalFile

/**
 * Moss analysis submission parameters.
 */
data class MossSubmission(val user: String,
                          val course: String,
                          val branch: String,
                          val base: List<LocalFile>,
                          val solutions: List<LocalFile>
) {
    val id = "$user/$course/$branch"
}