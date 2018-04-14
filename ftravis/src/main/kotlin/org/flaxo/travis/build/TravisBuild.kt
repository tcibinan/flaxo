package org.flaxo.travis.build

/**
 * Travis build interface.
 */
interface TravisBuild {

    /**
     * Status of the current build.
     */
    val status: BuildStatus

    /**
     * Branch travis build was performed for.
     */
    val branch: String
}
