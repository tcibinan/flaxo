import com.tcibinan.flaxo.core.env.frameworks.JUnitTestingFramework
import com.tcibinan.flaxo.core.env.languages.JavaLang
import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.tools.gradle.GradleBuildTool
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

        on("building environment ($language.$language.$framework)") {
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

fun performGradleTask(dir: File, task: String, vararg args: String): List<String> {
    val gradleHome = "/home/andrey/.sdkman/candidates/gradle/current"
    val gradlePath = gradleHome + "/bin"

    val process =
            ProcessBuilder(gradlePath + "/gradle", task, *args)
                    .directory(dir)
                    .start()

    return onlyOutput(completed(process))
}

fun perform(dir: File, command: String, vararg args: String): List<String> {
    val process =
            ProcessBuilder(command, *args)
                    .directory(dir)
                    .start()

    return onlyOutput(completed(process))
}

fun completed(process: Process): Pair<Int, List<String>> {
    process.waitFor()

    return process.exitValue() to process
            .inputStream.bufferedReader()
            .useLines { it.toList() }
}

fun onlyOutput(result: Pair<Int, List<String>>): List<String> {
    return if (result.first == 0)
        result.second
    else
        throw RuntimeException(result.second.joinToString(separator = System.lineSeparator()))
}

