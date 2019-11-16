package org.flaxo.cpp

import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.flaxo.common.cmd.CmdExecutor
import org.flaxo.common.deleteDirectoryRecursively
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.nio.file.Files

object CppBashEnvironmentSpec : Spek({
    val travisWebHookUrl = "http://travisWebHookUrl"
    val environment = CppBashEnvironment(travisWebHookUrl)

    describe("c++ bash environment") {
        it("should contain .travis.yml in the root") {
            environment.file(".travis.yml").shouldNotBeNull()
        }

        it("should contain .travis.yml with required webhook url") {
            val travisYml = environment.file(".travis.yml")

            travisYml.shouldNotBeNull()
            travisYml!!.content shouldContain "webhooks: $travisWebHookUrl"
        }

        it("should contain main .cpp file") {
            environment.file("src/main/cpp/main.cpp").shouldNotBeNull()
        }

        it("should contain example sources and headers") {
            environment.file("src/main/cpp/minus.cpp").shouldNotBeNull()
            environment.file("src/main/cpp/minus.h").shouldNotBeNull()
            environment.file("src/main/cpp/plus.cpp").shouldNotBeNull()
            environment.file("src/main/cpp/plus.h").shouldNotBeNull()
        }

        it("should contain example resources") {
            environment.file("src/test/resources/minus/negative_result/input.txt").shouldNotBeNull()
            environment.file("src/test/resources/minus/negative_result/output.txt").shouldNotBeNull()
            environment.file("src/test/resources/plus/positive_numbers/input.txt").shouldNotBeNull()
            environment.file("src/test/resources/plus/positive_numbers/output.txt").shouldNotBeNull()
            environment.file("src/test/resources/plus/negative_numbers/input.txt").shouldNotBeNull()
            environment.file("src/test/resources/plus/negative_numbers/output.txt").shouldNotBeNull()
        }

        it("should contain bash io tests scripts") {
            environment.file("run_test.sh").shouldNotBeNull()
            environment.file("run_tests.sh").shouldNotBeNull()
        }
    }

    describe("c++ bash environment building") {
        val tempBuildDirectory = Files.createTempDirectory("cppBashEnvironmentBuilding")

        afterGroup {
            deleteDirectoryRecursively(tempBuildDirectory)
        }

        on("building project") {
            environment.files().forEach { it.toLocalFile(tempBuildDirectory).flush() }

            val testOutput = with(CmdExecutor.within(tempBuildDirectory)) {
                execute("chmod", "+x", "run_tests.sh", "run_test.sh")
                execute("./run_tests.sh", "plus", "minus")
                        .drop(2)
                        .joinToString("\n")
            }

            it("should return successful log") {
                testOutput shouldEqual """
                    Tests:

                    > plus:
                    PASSED: negative_numbers
                    PASSED: positive_numbers

                    > minus:
                    PASSED: negative_result

                    Summary: SUCCESS
                """.trimIndent()
            }
        }
    }
})
