package com.tcibinan.flaxo.gradle

import com.tcibinan.flaxo.cmd.perform
import java.io.File

class GradleCmdExecutor private constructor(private val dir: File) {
    companion object {
        fun within(dir: File) = GradleCmdExecutor(dir)
    }

    fun build() = performTask("build")

    fun wrapper() = performTask("wrapper")

    private fun performTask(task: String, vararg args: String) =
            perform(dir, File("../gradlew").absolutePath, task, *args)

}