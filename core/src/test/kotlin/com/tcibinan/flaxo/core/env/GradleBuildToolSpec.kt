package com.tcibinan.flaxo.core.env

import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.tools.gradle.GradleDependency
import com.tcibinan.flaxo.core.env.tools.gradle.GradlePlugin
import com.tcibinan.flaxo.core.env.tools.gradle.GradleBuildTool
import com.tcibinan.flaxo.core.env.tools.gradle.GradleDependencyType.TEST_COMPILE
import com.tcibinan.flaxo.core.env.tools.gradle.GradleDependencyType.COMPILE_KOTLIN
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldHave
import io.kotlintest.matchers.substring
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

object GradleBuildToolSpec : SubjectSpek<BuildTool>({
    subject { GradleBuildTool() }

    val firstPlugin = GradlePlugin("java")
    val secondPlugin = GradlePlugin("application")
    val firstDependency = GradleDependency("a", "b", "c")
    val secondDependency = GradleDependency("1", "2", "3")
    val testingDependency = GradleDependency("t", "y", "u", type = TEST_COMPILE)
    val compileKotlinDependency = GradleDependency("t", "y", "u", type = COMPILE_KOTLIN)

    describe("Gradle build tool") {
        on("plugins addition") {
            val buildTool =
                    subject.addPlugin(firstPlugin)
                            .addPlugin(secondPlugin)
                            .addPlugin(secondPlugin)
            val environment = buildTool.buildEnvironment()
            val buildGradle = environment.getFiles()
                    .find { it.name() == "build.gradle" }
                    ?: throw Exception("build.gradle wasn't found")

            it("should have build.gradle with all passed plugins") {
                buildGradle.content() shouldHave substring("plugins {")
                buildGradle.shouldHaveName(firstPlugin, secondPlugin)
            }

            it("should have build.gradle with a single line for repeated plugins") {
                buildGradle.shouldHaveSingle(secondPlugin)
            }
        }

        on("dependency addition") {
            val buildTool =
                    subject.addDependency(firstDependency)
                            .addDependency(secondDependency)
                            .addDependency(secondDependency)
            val environment = buildTool.buildEnvironment()
            val buildGradle = environment.getFiles()
                    .find { it.name() == "build.gradle" }
                    ?: throw Exception("build.gradle wasn't found")

            it("should have build.gradle with build.gradle containing single dependency") {
                buildGradle.shouldHaveName(firstDependency, secondDependency)
            }

            it("should have build.gradle with a single line for repeated dependencies") {
                buildGradle.shouldHaveSingleName(secondDependency)
            }
        }

        on("different types of dependencies addition") {
            val buildTool =
                    subject.addDependency(testingDependency)
                            .addDependency(compileKotlinDependency)
            val environment = buildTool.buildEnvironment()
            val buildGradle = environment.getFiles()
                    .find { it.name() == "build.gradle" }
                    ?: throw Exception("build.gradle wasn't found")

            it("should contain all dependencies") {
                buildGradle.shouldHaveDependency(testingDependency, compileKotlinDependency)
            }

        }
    }
})

private fun File.shouldHaveName(vararg plugins: GradlePlugin) {
    plugins.forEach {
        content() shouldHave
                substring("apply plugin: \"${it.id}\"")
                        .or(substring("""id "${it.id}""""))
    }
}

private fun File.shouldHaveSingle(vararg plugins: GradlePlugin) {
    plugins.forEach {
        content()
                .split(
                        "apply plugin: \"${it.id}\"",
                        """id "${it.id}""""
                ).size - 1 shouldBe 1
    }
}

private fun File.shouldHaveName(vararg dependencies: GradleDependency) {
    dependencies.forEach {
        content() shouldHave substring(it.name())
    }
}

private fun File.shouldHaveDependency(vararg dependencies: GradleDependency) {
    dependencies.forEach {
        content() shouldHave substring(it.toString())
    }
}

private fun File.shouldHaveSingleName(vararg dependencies: GradleDependency) {
    dependencies.forEach {
        content()
                .split(it.name())
                .size - 1 shouldBe 1
    }
}