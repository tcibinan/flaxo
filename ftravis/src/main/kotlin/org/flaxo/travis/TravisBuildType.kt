package org.flaxo.travis

/**
 * Travis build type.
 */
enum class TravisBuildType(

        /**
         * Api parameter representation of build type.
         */
        val apiParam: String?
) {

    /**
     * Any build type.
     */
    ANY(null),

    /**
     * Pull request build type.
     */
    PULL_REQUEST("pull_request");
}