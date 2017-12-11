package com.tcibinan.flaxo.core.env.tools

private val spekVersion = "1.1.5"
private val junitVersion = "1.0.2"
private val kotlinVersion = "1.2.0"

fun javaPlugin() = GradlePlugin("java")

fun junitPlatformPlugin() =
        GradlePlugin(
                "org.junit.platform.gradle.plugin",
                junitVersion,
                setOf(GradleDependency("org.junit.platform", "junit-platform-gradle-plugin", junitVersion))
        )

fun kotlinTest() =
        GradleDependency("org.jetbrains.kotlin", "kotlin-test", kotlinVersion, test = true)

fun kotlinJreDependency(isTest: Boolean = false) =
        GradleDependency("org.jetbrains.kotlin", "kotlin-stdlib-jre8", kotlinVersion, test = isTest)

fun kotlinGradlePlugin() =
        GradlePlugin(
                "kotlin",
                kotlinVersion,
                setOf(GradleDependency("org.jetbrains.kotlin", "kotlin-gradle-plugin", kotlinVersion))
        )

fun spekJunitRunner() =
        GradleDependency("org.jetbrains.spek", "spek-junit-platform-engine", spekVersion, test = true)

fun spekSubject() =
        GradleDependency("org.jetbrains.spek", "spek-subject-extension", spekVersion, test = true)

fun spekDataDriven() =
        GradleDependency("org.jetbrains.spek", "spek-data-driven-extension", spekVersion, test = true)

fun spekApi() =
        GradleDependency("org.jetbrains.spek", "spek-api", spekVersion, test = true)