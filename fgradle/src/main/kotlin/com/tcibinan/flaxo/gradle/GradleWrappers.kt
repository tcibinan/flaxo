package com.tcibinan.flaxo.gradle

import com.tcibinan.flaxo.core.env.BinaryEnvironmentFile
import com.tcibinan.flaxo.core.env.EnvironmentFile
import com.tcibinan.flaxo.core.env.Environment
import com.tcibinan.flaxo.core.env.SimpleEnvironmentFile
import com.tcibinan.flaxo.core.env.SimpleEnvironment
import io.vavr.kotlin.Try
import java.io.File
import java.util.*

class GradleWrappers private constructor(files: Set<EnvironmentFile>)
    : Environment by SimpleEnvironment(files) {

    companion object {

        fun with(gradleBuild: EnvironmentFile): GradleWrappers {
            val dir = createTempDir("wrappers-generating-${Random().nextInt()}")
            dir.deleteOnExit()

            return Try(generateWrappers(dir, gradleBuild))
                    .onFailure {
                        dir.deleteRecursively()
                        throw GradleWrappersException(it)
                    }
                    .get()
        }

        private fun generateWrappers(dir: File, gradleBuild: EnvironmentFile): () -> GradleWrappers = {
            File(dir, gradleBuild.name()).fillWith(gradleBuild.content())

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
