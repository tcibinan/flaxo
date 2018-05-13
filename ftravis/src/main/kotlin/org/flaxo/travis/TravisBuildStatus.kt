package org.flaxo.travis

/**
 * Travis build status (state).
 */
enum class TravisBuildStatus(vararg states: String) {

    /**
     * Successful travis build status.
     */
    SUCCEED("Passed", "Fixed"),

    /**
     * Failed travis build status.
     */
    FAILED("Broken", "Failed", "Canceled", "Errored", "Still Failing"),

    /**
     * Pending travis build status.
     */
    IN_PROGRESS("Pending"),

    /**
     * Unsupported travis build status.
     */
    UNSUPPORTED;

    /**
     * Api parameter representations of build status.
     */
    val states = states

    companion object {
        fun retrieve(status: String): TravisBuildStatus =
                values().find { status in it.states }
                        ?: UNSUPPORTED
    }
}