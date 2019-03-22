package org.flaxo.common.data

/**
 * Github authentication parameters.
 */
data class GithubAuthData(

        /**
         * GitHub authorization url.
         */
        val redirectUrl: String,

        /**
         * GitHub authorization requests parameters.
         */
        val requestParams: Map<String, String>
)
