package com.tcibinan.flaxo.core.env.tools.gradle

import com.tcibinan.flaxo.core.env.tools.gradle.GradleDependencyType.*

private val spekVersion = "1.1.5"
private val junitVersion = "1.0.2"
private val kotlinVersion = "1.2.0"

//plugins

fun javaPlugin() = GradlePlugin("java")

fun junitPlatformPlugin() =
        GradlePlugin(
                "org.junit.platform.gradle.plugin",
                junitVersion,
                setOf(GradleDependency("org.junit.platform", "junit-platform-gradle-plugin", junitVersion))
        )

fun kotlinGradlePlugin() =
        GradlePlugin("kotlin", kotlinVersion,
                setOf(GradleDependency("org.jetbrains.kotlin", "kotlin-gradle-plugin", kotlinVersion))
        )

//dependencies

fun kotlinTest() =
        GradleDependency("org.jetbrains.kotlin", "kotlin-test", kotlinVersion, type = COMPILE_TEST)

fun kotlinJreDependency(type: GradleDependencyType = COMPILE) =
        GradleDependency("org.jetbrains.kotlin", "kotlin-stdlib-jre8", kotlinVersion, type = type)

fun spekJunitRunner() =
        GradleDependency("org.jetbrains.spek", "spek-junit-platform-engine", spekVersion, type = COMPILE_TEST)

fun spekSubject() =
        GradleDependency("org.jetbrains.spek", "spek-subject-extension", spekVersion, type = COMPILE_TEST)

fun spekDataDriven() =
        GradleDependency("org.jetbrains.spek", "spek-data-driven-extension", spekVersion, type = COMPILE_TEST)

fun spekApi() =
        GradleDependency("org.jetbrains.spek", "spek-api", spekVersion, type = COMPILE_TEST)