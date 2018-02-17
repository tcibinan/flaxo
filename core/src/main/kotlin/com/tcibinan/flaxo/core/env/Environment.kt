package com.tcibinan.flaxo.core.env

interface Environment {
    operator fun plus(environment: Environment): Environment
    operator fun plus(file: EnvironmentFile): Environment

    fun getFiles(): Set<EnvironmentFile>
    fun getFile(fileName: String): EnvironmentFile? =
            getFiles().firstOrNull { it.name() == fileName }
}