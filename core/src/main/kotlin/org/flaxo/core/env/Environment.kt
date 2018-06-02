package org.flaxo.core.env

/**
 * Repository environment interface.
 */
interface Environment {

    operator fun plus(environment: Environment): Environment

    operator fun plus(file: EnvironmentFile): Environment

    /**
     * Returns all files of the current environment.
     */
    fun getFiles(): Set<EnvironmentFile>

    /**
     * Returns file from the current environment by the given name.
     */
    fun getFile(fileName: String): EnvironmentFile? =
            getFiles().firstOrNull { it.path.toString() == fileName }
}