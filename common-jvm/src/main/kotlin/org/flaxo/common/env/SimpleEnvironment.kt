package org.flaxo.common.env

import org.flaxo.common.env.file.EnvironmentFile

/**
 * Environment implementation.
 */
class SimpleEnvironment(private val files: Set<EnvironmentFile>) : Environment {

    override operator fun plus(environment: Environment): Environment = SimpleEnvironment(files() + environment.files())

    override operator fun plus(file: EnvironmentFile): Environment = SimpleEnvironment(files() + file)

    override fun files(): Set<EnvironmentFile> = files
}
