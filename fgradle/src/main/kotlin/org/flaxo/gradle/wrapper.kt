package org.flaxo.gradle

import org.flaxo.common.env.Environment
import org.flaxo.common.env.file.ByteArrayEnvironmentFile
import org.flaxo.common.env.file.StringEnvironmentFile
import org.flaxo.common.env.SimpleEnvironment
import org.flaxo.common.env.file.EnvironmentFile

internal fun gradleWrappers(): Environment = SimpleEnvironment(setOf(
        gradlew(),
        gradlewBat(),
        gradleWrapperJar(),
        gradleWrapperProperties()
))

internal fun gradleWrapperProperties(): EnvironmentFile = StringEnvironmentFile(
        path = "gradle/wrapper/gradle-wrapper.properties",
        content = "../gradle/wrapper/gradle-wrapper.properties".lines()
)

internal fun gradleWrapperJar(): EnvironmentFile = ByteArrayEnvironmentFile(
        path = "gradle/wrapper/gradle-wrapper.jar",
        binaryContent = "../gradle/wrapper/gradle-wrapper.jar".bytes()
)

internal fun gradlewBat(): EnvironmentFile = StringEnvironmentFile(
        path = "gradlew.bat",
        content = "../gradlew.bat".lines()
)

internal fun gradlew(): EnvironmentFile = StringEnvironmentFile(
        path = "gradlew",
        content = "../gradlew".lines()
)
