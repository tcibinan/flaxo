package org.flaxo.gradle

import org.flaxo.core.env.BinaryEnvironmentFile
import org.flaxo.core.env.EnvironmentFile
import org.flaxo.core.env.Environment
import org.flaxo.core.env.SimpleEnvironmentFile
import org.flaxo.core.env.SimpleEnvironment
import io.vavr.kotlin.Try
import java.io.File
import java.util.*

class GradleWrappers private constructor(files: Set<EnvironmentFile>)
    : Environment by SimpleEnvironment(files) {

    companion object {

        fun with(gradleBuild: EnvironmentFile,
                 gradleSettings: EnvironmentFile
        ): GradleWrappers {
            val dir = createTempDir("wrappers-generating-${Random().nextInt()}")
            dir.deleteOnExit()

            return Try(generateWrappers(dir, gradleBuild, gradleSettings))
                    .onFailure {
                        dir.deleteRecursively()
                        throw GradleWrappersException(it)
                    }
                    .get()
        }

        private fun generateWrappers(dir: File,
                                     gradleBuild: EnvironmentFile,
                                     gradleSettings: EnvironmentFile
        ): () -> GradleWrappers = {
            File(dir, gradleBuild.name).fillWith(gradleBuild.content())
            File(dir, gradleSettings.name).fillWith(gradleSettings.content())

            GradleCmdExecutor.within(dir).wrapper()

            GradleWrappers(setOf(
                    "gradlew".loadFrom(dir),
                    "gradlew.bat".loadFrom(dir),
                    "gradle/wrapper/gradle-wrapper.jar".loadBinaryFrom(dir),
                    "gradle/wrapper/gradle-wrapper.properties".loadFrom(dir)
            ))
        }

        private fun String.loadFrom(dir: File): EnvironmentFile = let { filePath ->
            File(dir, this)
                    .useLines { it.toList() }
                    .joinToString("\n")
                    .let { content -> SimpleEnvironmentFile(filePath, content) }
        }

        private fun String.loadBinaryFrom(dir: File): EnvironmentFile =
                BinaryEnvironmentFile(this, File(dir, this).readBytes())

    }

    class GradleWrappersException(cause: Throwable)
        : Exception("Gradle wrappers weren't created due to underlying exception", cause)

}
