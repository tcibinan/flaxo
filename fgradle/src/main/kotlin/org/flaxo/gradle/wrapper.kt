package org.flaxo.gradle

import org.flaxo.common.env.Environment
import org.flaxo.common.env.file.ByteArrayEnvironmentFile
import org.flaxo.common.env.SimpleEnvironment
import org.flaxo.common.env.file.ClasspathEnvironmentFile
import org.flaxo.common.env.file.EnvironmentFile
import java.nio.file.Files
import java.nio.file.Paths

internal fun gradleWrappers(): Environment = SimpleEnvironment(setOf(
        gradlew(),
        gradlewBat(),
        gradleWrapperJar(),
        gradleWrapperProperties()
))

internal fun gradleWrapperProperties(): EnvironmentFile = "gradle/wrapper/gradle-wrapper.properties".let {
    localFile(it, it)
            ?: localFile(localPath = "../gradle/wrapper/gradle-wrapper.properties", path = it)
            ?: classpathFile(it)
}

internal fun gradleWrapperJar(): EnvironmentFile = "gradle/wrapper/gradle-wrapper.jar".let {
    localFile(it, it)
            ?: localFile(localPath = "../gradle/wrapper/gradle-wrapper.jar", path = it)
            ?: classpathFile(it)
}

internal fun gradlewBat(): EnvironmentFile = "gradlew.bat".let {
    localFile(it, it)
            ?: localFile(localPath = "../gradlew.bat", path = it)
            ?: classpathFile((it))
}

internal fun gradlew(): EnvironmentFile = "gradlew".let {
    localFile(it, it)
            ?: localFile(localPath = "../gradlew", path = it)
            ?: classpathFile(it)
}

private fun localFile(localPath: String, path: String): ByteArrayEnvironmentFile? {
    return Paths.get(localPath)
            .takeIf { Files.isRegularFile(it) }
            ?.let { ByteArrayEnvironmentFile(path = path, binaryContent = localPath.bytes()) }
}

private fun classpathFile(path: String) =
        ClasspathEnvironmentFile(classpathPath = classpathPath(path), path = Paths.get(path))

private fun classpathPath(path: String) = Paths.get("bundle").resolve(path)
