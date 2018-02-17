import com.tcibinan.flaxo.cmd.perform
import com.tcibinan.flaxo.core.framework.JUnitTestingFramework
import com.tcibinan.flaxo.core.language.JavaLang
import com.tcibinan.flaxo.core.build.BuildTool
import com.tcibinan.flaxo.gradle.GradleBuildTool
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
    val travisFileName = ".travis.yml"

    subject { GradleBuildTool(JavaLang, JavaLang, JUnitTestingFramework) }

    describe("gradle environment") {

        on("creating environment") {
            val environment =
                    subject.withLanguage(language)
                            .withTestingsLanguage(language)
                            .withTestingFramework(framework)
                            .produceEnvironment()



            it("should create non-empty $travisFileName") {
                val travisFile = environment.getFiles()
                        .find { it.name() == travisFileName }
                        ?: throw Exception("$travisFileName wasn't found")

                assertTrue { travisFile.content().isNotBlank() }
            }

            it("should create non-empty $gradleFileName") {
                val gradleFile = environment.getFiles()
                        .find { it.name() == gradleFileName }
                        ?: throw Exception("$gradleFileName wasn't found")

                assertTrue { gradleFile.content().isNotBlank() }
            }
        }

        on("performing `gradle build` with the environment of ($language + $language + $framework)") {
            val environment =
                    subject.withLanguage(language)
                            .withTestingsLanguage(language)
                            .withTestingFramework(framework)
                            .produceEnvironment()

            val buildFile = environment.getFiles()
                    .find { it.name() == gradleFileName }
                    ?: throw Exception("$gradleFileName wasn't found")

            it("should create buildable project") {
                val tempDir = createTempDir("$language.$language.$framework")
                tempDir.deleteOnExit()

                perform(tempDir, "touch", gradleFileName)
                writeToFile(tempDir, gradleFileName, buildFile.content())
                performGradleTask(tempDir, "build")
            }
        }
    }
})

fun writeToFile(dir: File, destinationFile: String, content: String) {
    File(dir, destinationFile)
            .outputStream().bufferedWriter()
            .use { it.write(content) }
}

fun performGradleTask(dir: File, task: String, vararg args: String) =
        perform(dir, File("../gradlew").absolutePath, task, *args)


