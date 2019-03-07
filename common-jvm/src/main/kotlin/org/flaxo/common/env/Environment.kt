package org.flaxo.common.env

import org.flaxo.common.env.file.EnvironmentFile
import java.nio.file.Paths

/**
 * Repository environment.
 */
interface Environment {

    /**
     * Returns a new environment that contains all [files] from the original environment and
     * the specified [environment].
     */
    operator fun plus(environment: Environment): Environment =
            throw UnsupportedOperationException("Environment.plus is not supported.")

    /**
     * Returns a new environment that contains all [files] as well as the specified [file].
     */
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

        /**
         * Returns an empty environment.
         */
        fun empty() = empty
    }
}
