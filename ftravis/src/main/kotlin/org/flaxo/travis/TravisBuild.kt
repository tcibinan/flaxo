package org.flaxo.travis

import java.time.LocalDateTime

/**
 * Travis build interface.
 */
interface TravisBuild {

    /**
     * Status of the current build.
     */
    val buildStatus: TravisBuildStatus

    /**
     * Branch travis build was performed for.
     */
    val branch: String

    /**
     * Build commit sha.
     */
    val commitSha: String

    /**
     * Building finished date.
     */
    val finishedAt: LocalDateTime?
}
