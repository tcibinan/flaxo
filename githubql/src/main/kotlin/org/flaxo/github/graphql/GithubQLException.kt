package org.flaxo.github.graphql

import org.flaxo.core.FlaxoException

class GithubQLException(message: String, cause: Throwable? = null)
    : FlaxoException(message, cause)
