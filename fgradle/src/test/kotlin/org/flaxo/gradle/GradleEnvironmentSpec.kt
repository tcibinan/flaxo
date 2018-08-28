package org.flaxo.gradle

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.amshove.kluent.shouldNotBeNull
import org.flaxo.core.env.Environment
import org.flaxo.core.env.EnvironmentSupplier
import org.flaxo.core.env.SimpleEnvironment
import org.flaxo.core.env.file.StringEnvironmentFile
import org.flaxo.core.framework.JUnitTestingFramework
import org.flaxo.core.language.JavaLang
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertTrue

object GradleEnvironmentSpec : SubjectSpek<GradleBuildTool>({

    val language = JavaLang
    val framework = JUnitTestingFramework
    val gradleFileName = "build.gradle"
    val travisFiles = setOf(
            StringEnvironmentFile("travisfile1", "travisfile1content"),
            StringEnvironmentFile("travisfile2", "travisfile1content")
    )
    val travis: EnvironmentSupplier = mock {
        on { with(any(), any(), any()) }.thenReturn(it)
        on { environment() }.thenReturn(SimpleEnvironment(travisFiles))
    }
    subject {
        GradleBuildTool(travis)
                .with(JavaLang, JavaLang, JUnitTestingFramework)
                as GradleBuildTool
    }

    describe("gradle environment") {

        on("creating environment") {
            val environment =
                    subject.with(language, language, framework)
                            .environment()

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
                    environment.file(it.fileName).shouldNotBeNull()
                }
            }
        }
    }
})

class EnvironmentFileNotFound(message: String) : RuntimeException(message)

private fun Environment.fileIsNotBlank(fileName: String): Boolean =
        file(fileName)
                ?.content
                ?.isNotBlank()
                ?: throw EnvironmentFileNotFound("$fileName wasn't found in the environment")

private fun Environment.binaryFileIsNotEmpty(fileName: String): Boolean =
        file(fileName)
                ?.binaryContent
                ?.isNotEmpty()
                ?: throw EnvironmentFileNotFound("$fileName wasn't found in the environment")