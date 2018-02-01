package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.rest.CoreConfiguration
import com.tcibinan.flaxo.rest.services.RepositoryEnvironmentService
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.io.File


object GradleEnvironmentSpec : SubjectSpek<RepositoryEnvironmentService>({
    val language = "java"
    val framework = "junit"
    val buildFileName = "build.gradle"
    val context = AnnotationConfigApplicationContext(CoreConfiguration::class.java)

    subject { context.getBean("repositoryEnvironmentService", RepositoryEnvironmentService::class.java) }

    describe("environment") {

        on("building environment ($language.$language.$framework)") {
            val environment = subject.produceEnvironment(language, language, framework)
            val buildFile = environment.getFiles()
                    .find { it.name() == buildFileName }
                    ?: throw Exception("$buildFileName wasn't found")

            it("should build project") {
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

