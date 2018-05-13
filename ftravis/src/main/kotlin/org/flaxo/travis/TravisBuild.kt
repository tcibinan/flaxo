package org.flaxo.travis

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
}
