package com.tcibinan.flaxo.core.env

class SimpleEnvironment(private val files: Set<EnvironmentFile>) : Environment {

    override operator fun plus(environment: Environment): Environment =
            SimpleEnvironment(getFiles() + environment.getFiles())

    override operator fun plus(file: EnvironmentFile): Environment =
            SimpleEnvironment(getFiles() + file)

    override fun getFiles(): Set<EnvironmentFile> = files
}