package org.flaxo.common

/**
 * Github authentication parameters.
 */
class GithubAuthData(
        val redirectUrl: String,
        val requestParams: Map<String, String>
)
