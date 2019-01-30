package org.flaxo.common.env

import org.flaxo.common.env.file.EnvironmentFile
import java.nio.file.Paths

/**
 * Repository environment.
 */
interface Environment {

    operator fun plus(environment: Environment): Environment =
            throw UnsupportedOperationException("Environment.plus is not supported.")

    operator fun plus(file: EnvironmentFile): Environment =
            throw UnsupportedOperationException("Environment.plus is not supported.")

    /**
     * Returns all environment files.
     */
    fun files(): Set<EnvironmentFile>

    /**
     * Returns file from the current environment by the given [path].
     */
    fun file(path: String): EnvironmentFile? =
            files().firstOrNull { it.path == Paths.get(path) }

    companion object {
        private val empty = SimpleEnvironment(emptySet())
        fun empty() = empty
    }
}
