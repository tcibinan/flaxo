package org.flaxo.common.data

/**
 * Github authentication parameters.
 */
class GithubAuthData(
        val redirectUrl: String,
        val requestParams: Map<String, String>
)
