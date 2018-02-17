package com.tcibinan.flaxo.gradle

import com.tcibinan.flaxo.cmd.CmdExecutor
import java.io.File

class GradleCmdExecutor private constructor(private val dir: File?) {
    companion object {
        fun within(dir: File? = null) = GradleCmdExecutor(dir)
    }

    fun build() = performTask("build")

    fun wrapper() = performTask("wrapper")

    private fun performTask(task: String, vararg args: String) =
            CmdExecutor.within(dir)
                    .execute(File("../gradlew").absolutePath, task, *args)

}