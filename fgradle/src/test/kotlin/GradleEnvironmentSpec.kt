import com.nhaarman.mockito_kotlin.mock
import com.tcibinan.flaxo.cmd.CmdExecutor
import com.tcibinan.flaxo.core.build.BuildTool
import com.tcibinan.flaxo.core.env.Environment
import com.tcibinan.flaxo.core.env.EnvironmentSupplier
import com.tcibinan.flaxo.core.env.SimpleEnvironment
import com.tcibinan.flaxo.core.env.SimpleEnvironmentFile
import com.tcibinan.flaxo.core.framework.JUnitTestingFramework
import com.tcibinan.flaxo.core.language.JavaLang
import com.tcibinan.flaxo.gradle.GradleBuildTool
import com.tcibinan.flaxo.gradle.GradleCmdExecutor
import com.tcibinan.flaxo.gradle.fillWith
import io.kotlintest.matchers.shouldNotBe
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import java.io.File
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
        on { getEnvironment() }.thenReturn(SimpleEnvironment(travisFiles))
    }

    subject { GradleBuildTool(JavaLang, JavaLang, JUnitTestingFramework, travis) }

    describe("gradle environment") {

        on("creating environment") {
            val environment =
                    subject.withLanguage(language)
                            .withTestingsLanguage(language)
                            .withTestingFramework(framework)
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
                    environment.getFile(it.name()) shouldNotBe null
                }
            }
        }

        on("performing `gradle build` with the environment of ($language + $language + $framework)") {
            val environment =
                    subject.withLanguage(language)
                            .withTestingsLanguage(language)
                            .withTestingFramework(framework)
                            .getEnvironment()

            val buildFile = environment.getFile(gradleFileName)!!

            it("should create buildable project") {
                val tempDir = createTempDir("$language.$language.$framework")
                tempDir.deleteOnExit()

                CmdExecutor.within(tempDir).execute("touch", gradleFileName)
                File(tempDir, gradleFileName).fillWith(buildFile.content())
                GradleCmdExecutor.within(tempDir).build()
            }
        }
    }
})

private fun Environment.fileIsNotBlank(fileName: String): Boolean =
        getFile(fileName)!!.content().isNotBlank()

private fun Environment.binaryFileIsNotEmpty(fileName: String): Boolean =
        getFile(fileName)!!.binaryContent().isNotEmpty()