package org.flaxo.github.graphql

import org.flaxo.common.FlaxoException

class GithubQLException(message: String, cause: Throwable? = null)
    : FlaxoException(message, cause)
