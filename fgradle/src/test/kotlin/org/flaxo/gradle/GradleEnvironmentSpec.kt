package org.flaxo.gradle

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.flaxo.core.build.BuildTool
import org.flaxo.core.env.Environment
import org.flaxo.core.env.EnvironmentSupplier
import org.flaxo.core.env.SimpleEnvironment
import org.flaxo.core.env.SimpleEnvironmentFile
import org.flaxo.core.framework.JUnitTestingFramework
import org.flaxo.core.language.JavaLang
import io.kotlintest.matchers.shouldNotBe
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertTrue

object GradleEnvironmentSpec : SubjectSpek<BuildTool>({

    val language = JavaLang
    val framework = JUnitTestingFramework
    val gradleFileName = "build.gradle"
    val travisFiles = setOf(
            SimpleEnvironmentFile("travisfile1", "travisfile1content"),
            SimpleEnvironmentFile("travisfile2", "travisfile1content")
    )
    val travis: EnvironmentSupplier = mock {
        on { withLanguage(any()) }.thenReturn(it)
        on { withTestingLanguage(any()) }.thenReturn(it)
        on { withTestingFramework(any()) }.thenReturn(it)
        on { getEnvironment() }.thenReturn(SimpleEnvironment(travisFiles))
    }
    subject {
        GradleBuildTool(travis)
                .with(JavaLang, JavaLang, JUnitTestingFramework)
                as BuildTool
    }

    describe("gradle environment") {

        on("creating environment") {
            val environment =
                    subject.with(language, language, framework)
                            .getEnvironment()

            it("should contain non blank $gradleFileName") {
                assertTrue {
                    environment.fileIsNotBlank(gradleFileName)
                }
            }

            it("should contain non blank gradlew") {
                assertTrue {
                    environment.fileIsNotBlank("gradlew")
                }
            }

            it("should contain non blank gradlew.bat") {
                assertTrue {
                    environment.fileIsNotBlank("gradlew.bat")
                }
            }

            it("should contain non blank gradle/wrapper/gradle-wrapper.jar") {
                assertTrue {
                    environment.binaryFileIsNotEmpty("gradle/wrapper/gradle-wrapper.jar")
                }
            }

            it("should contain non blank gradle/wrapper/gradle-wrapper.properties") {
                assertTrue {
                    environment.fileIsNotBlank("gradle/wrapper/gradle-wrapper.properties")
                }
            }

            it("should contain all files from travis environment supplier") {
                travisFiles.forEach {
                    environment.getFile(it.name) shouldNotBe null
                }
            }
        }
    }
})

class EnvironmentFileNotFound(message: String) : RuntimeException(message)

private fun Environment.fileIsNotBlank(fileName: String): Boolean =
        getFile(fileName)
                ?.content()
                ?.isNotBlank()
                ?: throw EnvironmentFileNotFound("$fileName wasn't found in the environment")

private fun Environment.binaryFileIsNotEmpty(fileName: String): Boolean =
        getFile(fileName)
                ?.binaryContent()
                ?.isNotEmpty()
                ?: throw EnvironmentFileNotFound("$fileName wasn't found in the environment")