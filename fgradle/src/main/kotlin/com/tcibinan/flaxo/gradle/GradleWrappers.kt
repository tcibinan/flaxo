package com.tcibinan.flaxo.gradle

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
                    "gradlew".loadFromDir(dir),
                    "gradlew.bat".loadFromDir(dir),
                    "gradle/wrapper/gradle-wrapper.jar".loadFromDir(dir),
                    "gradle/wrapper/gradle-wrapper.properties".loadFromDir(dir)
            ))
        }

        private fun String.loadFromDir(dir: File): EnvironmentFile =
                File(dir, this)
                        .useLines { it.toList() }
                        .joinToString("\n")
                        .run { SimpleEnvironmentFile(this@loadFromDir, this) }

    }

    class GradleWrappersException(cause: Throwable)
        : Exception("Gradle wrappers weren't created due to underlying exception", cause)

}
