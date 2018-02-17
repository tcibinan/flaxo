package com.tcibinan.flaxo.core.env

class SimpleEnvironment(private val files: Set<File>) : Environment {
    override fun getFiles(): Set<File> = files

    override fun plus(environment: Environment): Environment =
            SimpleEnvironment(getFiles() + environment.getFiles())
}