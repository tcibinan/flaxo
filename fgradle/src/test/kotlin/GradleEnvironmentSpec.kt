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


object GradleEnvironmentSpec : SubjectSpek<BuildTool>({
    val language = JavaLang
    val framework = JUnitTestingFramework
    val buildFileName = "build.gradle"

    subject { GradleBuildTool() }

    describe("gradle environment") {

        on("building environment ($language + $language + $framework)") {
            val environment =
                    subject.withLanguage(language)
                            .withTestingsLanguage(language)
                            .withTestingFramework(framework)
                            .buildEnvironment()

            val buildFile = environment.getFiles()
                    .find { it.name() == buildFileName }
                    ?: throw Exception("$buildFileName wasn't found")

            it("should create buildable project") {
                val tempDir = createTempDir("$language.$language.$framework")
                tempDir.deleteOnExit()

                perform(tempDir, "touch", buildFileName)
                writeToFile(tempDir, buildFileName, buildFile.content())
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


