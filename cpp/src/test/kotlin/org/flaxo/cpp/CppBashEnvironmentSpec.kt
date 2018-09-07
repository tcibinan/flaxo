package org.flaxo.cpp

import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.flaxo.cmd.CmdExecutor
import org.flaxo.core.deleteDirectoryRecursively
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.nio.file.Files

object CppBashEnvironmentSpec : Spek({
    val environment = CppBashEnvironment()

    describe("c++ bash environment") {
        it("should contain .travis.yml in the root") {
            environment.file(".travis.yml").shouldNotBeNull()
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
            environment.files().forEach { it.toLocalFile(tempBuildDirectory) }

            val testOutput = CmdExecutor.within(tempBuildDirectory)
                    .execute("./run_tests.sh", "plus", "minus")
                    .drop(2)
                    .joinToString("\n")

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