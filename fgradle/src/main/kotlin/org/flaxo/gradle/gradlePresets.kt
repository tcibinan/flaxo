package org.flaxo.gradle

import org.flaxo.gradle.GradleDependencyType.*

private const val spekVersion = "1.1.5"
private const val junitVersion = "1.0.3"
private const val kotlinVersion = "1.2.31"
private const val jupiterVersion = "5.0.3"

//repositories

internal fun mavenCentral() = GradleRepository("mavenCentral()")
internal fun jcenter() = GradleRepository("jcenter()")
internal fun gradlePluginPortal() = GradleRepository("gradlePluginPortal()")
internal fun jcenterBinTray() = GradleRepository("maven { url \"https://jcenter.bintray.com/\" }")

//plugins

internal fun javaPlugin() = GradlePlugin("java")

internal fun junitPlatformPlugin() =
        GradlePlugin(
                "org.junit.platform.gradle.plugin",
                junitVersion,
                setOf(junitPlatformPluginDependency()),
                junitPlatformPluginManagement()
        )

internal fun kotlinGradleJvmPlugin() =
        GradlePlugin(
                "org.jetbrains.kotlin.jvm",
                kotlinVersion
        )

//plugin management

internal fun junitPlatformPluginManagement() =
        GradlePluginManagement(
                "org.junit.platform.gradle.plugin",
                GradleDependency(
                        "org.junit.platform",
                        "junit-platform-gradle-plugin",
                        junitVersion
                ),
                setOf(gradlePluginPortal(), jcenterBinTray())
        )

//dependencies

internal fun kotlinTestDependency() =
        GradleDependency(
                "org.jetbrains.kotlin",
                "kotlin-test",
                kotlinVersion,
                type = TEST_COMPILE
        )

internal fun kotlinJreDependency(type: GradleDependencyType = COMPILE) =
        GradleDependency(
                "org.jetbrains.kotlin",
                "kotlin-stdlib-jre8",
                kotlinVersion,
                type = type
        )

internal fun spekJunitRunnerDependency() =
        GradleDependency(
                "org.jetbrains.spek",
                "spek-junit-platform-engine",
                spekVersion,
                type = TEST_COMPILE
        )

internal fun spekSubjectDependency() =
        GradleDependency(
                "org.jetbrains.spek",
                "spek-subject-extension",
                spekVersion,
                type = TEST_COMPILE
        )

internal fun spekDataDrivenDependency() =
        GradleDependency(
                "org.jetbrains.spek",
                "spek-data-driven-extension",
                spekVersion,
                type = TEST_COMPILE
        )

internal fun spekApiDependency() =
        GradleDependency(
                "org.jetbrains.spek",
                "spek-api",
                spekVersion,
                type = TEST_COMPILE
        )

internal fun junitPlatformPluginDependency() =
        GradleDependency(
                "org.junit.platform",
                "junit-platform-gradle-plugin",
                junitVersion,
                type = CLASSPATH
        )

internal fun jupiterApiDependency() =
        GradleDependency(
                "org.junit.jupiter",
                "junit-jupiter-api",
                jupiterVersion,
                type = TEST_COMPILE
        )

internal fun jupiterEngineDependency() =
        GradleDependency(
                "org.junit.jupiter",
                "junit-jupiter-engine",
                jupiterVersion,
                type = TEST_RUNTIME
        )