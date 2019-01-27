package org.flaxo.gradle

import org.flaxo.common.cmd.CmdExecutor
import java.nio.file.Path
import java.nio.file.Paths

internal class GradleCmdExecutor private constructor(private val directory: Path?) {

    companion object {
        fun within(directory: Path? = null) = GradleCmdExecutor(directory)
    }

    fun build() = performTask("build")

    private fun performTask(task: String, vararg args: String) =
            CmdExecutor.within(directory)
                    .execute(Paths.get("../gradlew").toAbsolutePath().toString(), task, *args)

}