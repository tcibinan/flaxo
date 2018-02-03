package com.tcibinan.flaxo.gradle

import com.tcibinan.flaxo.gradle.GradleDependencyType.*

private val spekVersion = "1.1.5"
private val junitVersion = "1.0.3"
private val kotlinVersion = "1.2.0"
private val jupiterVersion = "5.0.3"

//repositories

fun mavenCentral() = GradleRepository("mavenCentral()")
fun jcenter() = GradleRepository("jcenter()")

//plugins

fun javaPlugin() = GradlePlugin("java")

fun junitPlatformPlugin() =
        GradlePlugin(
                "org.junit.platform.gradle.plugin",
                junitVersion,
                setOf(junitPlatformPluginDependency())
        )

fun kotlinGradlePlugin() =
        GradlePlugin(
                "kotlin",
                kotlinVersion,
                setOf(kotlinGradlePluginDependency())
        )

//dependencies

fun kotlinTestDependency() =
        GradleDependency(
                "org.jetbrains.kotlin",
                "kotlin-test",
                kotlinVersion,
                type = TEST_COMPILE
        )

fun kotlinJreDependency(type: GradleDependencyType = COMPILE) =
        GradleDependency(
                "org.jetbrains.kotlin",
                "kotlin-stdlib-jre8",
                kotlinVersion,
                type = type
        )

fun spekJunitRunnerDependency() =
        GradleDependency(
                "org.jetbrains.spek",
                "spek-junit-platform-engine",
                spekVersion,
                type = TEST_COMPILE
        )

fun spekSubjectDependency() =
        GradleDependency(
                "org.jetbrains.spek",
                "spek-subject-extension",
                spekVersion,
                type = TEST_COMPILE
        )

fun spekDataDrivenDependency() =
        GradleDependency(
                "org.jetbrains.spek",
                "spek-data-driven-extension",
                spekVersion,
                type = TEST_COMPILE
        )

fun spekApiDependency() =
        GradleDependency(
                "org.jetbrains.spek",
                "spek-api",
                spekVersion,
                type = TEST_COMPILE
        )

fun junitPlatformPluginDependency() =
        GradleDependency(
                "org.junit.platform",
                "junit-platform-gradle-plugin",
                junitVersion,
                type = CLASSPATH
        )
fun kotlinGradlePluginDependency() =
        GradleDependency(
                "org.jetbrains.kotlin",
                "kotlin-gradle-plugin",
                kotlinVersion
        )

fun jupiterApiDependency() =
        GradleDependency(
                "org.junit.jupiter",
                "junit-jupiter-api",
                jupiterVersion,
                type = TEST_COMPILE
        )

fun jupiterEngineDependency() =
        GradleDependency(
                "org.junit.jupiter",
                "junit-jupiter-engine",
                jupiterVersion,
                type = TEST_RUNTIME
        )