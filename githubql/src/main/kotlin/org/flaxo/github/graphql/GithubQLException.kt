package org.flaxo.github.graphql

import org.flaxo.common.FlaxoException

/**
 * GitHub GraphQL call exception.
 */
class GithubQLException(message: String, cause: Throwable? = null)
    : FlaxoException(message, cause)
