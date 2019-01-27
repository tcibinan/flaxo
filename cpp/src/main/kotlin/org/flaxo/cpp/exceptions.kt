package org.flaxo.cpp

import org.flaxo.common.FlaxoException

/**
 *  C++ environment exception.
 */
class CppEnvironmentException(message: String? = null, cause: Throwable? = null)
    : FlaxoException(message, cause)
