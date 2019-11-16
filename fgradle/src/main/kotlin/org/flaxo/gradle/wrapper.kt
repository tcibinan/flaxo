package org.flaxo.gradle

import org.flaxo.common.env.Environment
import org.flaxo.common.env.SimpleEnvironment
import org.flaxo.common.env.file.ClasspathEnvironmentFile
import org.flaxo.common.env.file.EnvironmentFile
import org.flaxo.common.env.file.LocalEnvironmentFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

internal fun gradleWrappers(): Environment = SimpleEnvironment(setOf(
        gradlew(),
        gradlewBat(),
        gradleWrapperJar(),
        gradleWrapperProperties()
))

internal fun gradleWrapperProperties(): EnvironmentFile = path("gradle/wrapper/gradle-wrapper.properties").let {
    localFile(it, it)
            ?: localFile(localPath = path("../gradle/wrapper/gradle-wrapper.properties"), path = it)
            ?: classpathFile(it)
}

internal fun gradleWrapperJar(): EnvironmentFile = path("gradle/wrapper/gradle-wrapper.jar").let {
    localFile(it, it)
            ?: localFile(localPath = path("../gradle/wrapper/gradle-wrapper.jar"), path = it)
            ?: classpathFile(it)
}

internal fun gradlewBat(): EnvironmentFile = path("gradlew.bat").let {
    localFile(it, it)
            ?: localFile(localPath = path("../gradlew.bat"), path = it)
            ?: classpathFile((it))
}

internal fun gradlew(): EnvironmentFile = path("gradlew").let {
    localFile(it, it)
            ?: localFile(localPath = path("../gradlew"), path = it)
            ?: classpathFile(it)
}

private fun path(path: String): Path = Paths.get(path)

private fun localFile(localPath: Path, path: Path): EnvironmentFile? {
    return localPath
            .takeIf { Files.isRegularFile(it) }
            ?.let { LocalEnvironmentFile(localPath = it, path = path) }
}

private fun classpathFile(path: Path) =
        ClasspathEnvironmentFile(classpathPath = classpathPath(path), path = path)

private fun classpathPath(path: Path) = Paths.get("bundle").resolve(path)
