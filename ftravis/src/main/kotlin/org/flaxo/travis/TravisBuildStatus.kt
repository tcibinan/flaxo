package org.flaxo.travis

/**
 * Travis build status (state).
 */
enum class TravisBuildStatus(

        /**
         * Api parameter representations of build status.
         */
        vararg val states: String
) {

    /**
     * Successful travis build status.
     */
    SUCCEED("Passed", "Fixed",
            "passed", "fixed"),

    /**
     * Failed travis build status.
     */
    FAILED("Broken", "Failed", "Canceled", "Errored", "Still Failing",
            "broken", "failed", "canceled", "errored", "still failing"),

    /**
     * Pending travis build status.
     */
    IN_PROGRESS("Pending",
            "pending"),

    /**
     * Unsupported travis build status.
     */
    UNSUPPORTED;

    companion object {

        /**
         * Parses status string and returns a corresponding travis build status.
         */
        fun retrieve(status: String): TravisBuildStatus = values().find { status in it.states } ?: UNSUPPORTED
    }
}
