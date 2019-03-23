package org.flaxo.common.data

import kotlinx.serialization.Serializable

/**
 * Github authentication parameters.
 */
@Serializable
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
