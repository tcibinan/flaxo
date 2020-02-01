package org.flaxo.rest.manager.gitplag

import org.flaxo.common.FlaxoException

/**
 * Gitplag exception.
 */
class GitplagException(message: String? = null, cause: Throwable? = null) : FlaxoException(message, cause)
