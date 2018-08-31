package org.flaxo.core.env

import org.flaxo.core.env.file.EnvironmentFile

/**
 * Environment implementation.
 */
class SimpleEnvironment(private val files: Set<EnvironmentFile>) : Environment {

    override operator fun plus(environment: Environment): Environment = SimpleEnvironment(files() + environment.files())

    override operator fun plus(file: EnvironmentFile): Environment = SimpleEnvironment(files() + file)

    override fun files(): Set<EnvironmentFile> = files
}
