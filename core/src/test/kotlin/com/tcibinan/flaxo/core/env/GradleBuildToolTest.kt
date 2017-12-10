package com.tcibinan.flaxo.core.env

import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.tools.GradleDependency
import com.tcibinan.flaxo.core.env.tools.GradlePlugin
import com.tcibinan.flaxo.core.env.tools.GradleBuildTool
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldHave
import io.kotlintest.matchers.substring
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

object GradleBuildToolSpec: SubjectSpek<BuildTool>({
    subject { GradleBuildTool() }

    val firstPlugin = GradlePlugin("java")
    val secondPlugin = GradlePlugin("application")
    val firstDependency = GradleDependency("a", "b", "c")
    val secondDependency = GradleDependency("1", "2", "3")

    describe("Gradle build tool") {
        on("plugins addition") {
            val buildTool = subject.addPlugin(firstPlugin)
                    .addPlugin(secondPlugin)
                    .addPlugin(secondPlugin)
            val environment = buildTool.buildEnvironment()
            val buildGradle = environment.getFiles().find { it.named("build.gradle") }
                    ?: throw Exception("build.gradle wasn't found")

            it("should have build.gradle with all passed plugins") {
                buildGradle.content() shouldHave substring("plugins {")
                buildGradle.shouldHave(firstPlugin, secondPlugin)
            }

            it("should have build.gradle with a single line for repeated plugins") {
                buildGradle.shouldHaveSingle(secondPlugin)
            }
        }

        on("dependency addition") {
            val buildTool = subject.addDependency(firstDependency)
                    .addDependency(secondDependency)
                    .addDependency(secondDependency)
            val environment = buildTool.buildEnvironment()
            val buildGradle = environment.getFiles().find { it.named("build.gradle") }
                    ?: throw Exception("build.gradle wasn't found")

            it("should have build.gradle with build.gradle containing single dependency") {
                buildGradle.shouldHave(firstDependency, secondDependency)
            }

            it("should have build.gradle with a single line for repeated dependencies") {
                buildGradle.shouldHaveSingle(secondDependency)
            }
        }
    }
})

private fun File.named(name: String): Boolean = this.name() == name

private fun File.shouldHave(vararg plugins: GradlePlugin) {
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

private fun File.shouldHave(vararg dependencies: GradleDependency) {
    dependencies.forEach {
        content() shouldHave
                substring("${it.group}:${it.name}:${it.version}")
    }
}

private fun File.shouldHaveSingle(vararg dependencies: GradleDependency) {
    dependencies.forEach {
        content()
                .split("${it.group}:${it.name}:${it.version}")
                .size - 1 shouldBe 1
    }
}