import com.tcibinan.flaxo.cmd.CmdExecutor
import com.tcibinan.flaxo.core.framework.JUnitTestingFramework
import com.tcibinan.flaxo.core.language.JavaLang
import com.tcibinan.flaxo.core.build.BuildTool
import com.tcibinan.flaxo.core.env.Environment
import com.tcibinan.flaxo.gradle.GradleBuildTool
import com.tcibinan.flaxo.gradle.GradleCmdExecutor
import com.tcibinan.flaxo.gradle.fillWith
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

    subject { GradleBuildTool(JavaLang, JavaLang, JUnitTestingFramework) }

    describe("gradle environment") {

        on("creating environment") {
            val environment =
                    subject.withLanguage(language)
                            .withTestingsLanguage(language)
                            .withTestingFramework(framework)
                            .produceEnvironment()

            it("should create non-empty $gradleFileName") {
                assertTrue {
                    environment.fileIsNotBlank(gradleFileName)
                }
            }

            it("should create non-empty .travis.yml") {
                assertTrue {
                    environment.fileIsNotBlank(".travis.yml")
                }
            }

            it("should create non-empty gradlew") {
                assertTrue {
                    environment.fileIsNotBlank("gradlew")
                }
            }

            it("should create non-empty gradlew.bat") {
                assertTrue {
                    environment.fileIsNotBlank("gradlew.bat")
                }
            }

            it("should create non-empty gradle/wrapper/gradle-wrapper.jar") {
                assertTrue {
                    environment.binaryFileIsNotEmpty("gradle/wrapper/gradle-wrapper.jar")
                }
            }

            it("should create non-empty gradle/wrapper/gradle-wrapper.properties") {
                assertTrue {
                    environment.fileIsNotBlank("gradle/wrapper/gradle-wrapper.properties")
                }
            }
        }

        on("performing `gradle build` with the environment of ($language + $language + $framework)") {
            val environment =
                    subject.withLanguage(language)
                            .withTestingsLanguage(language)
                            .withTestingFramework(framework)
                            .produceEnvironment()

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