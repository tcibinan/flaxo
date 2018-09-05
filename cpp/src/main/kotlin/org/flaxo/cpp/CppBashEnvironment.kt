package org.flaxo.cpp

import org.flaxo.core.env.Environment
import org.flaxo.core.env.SimpleEnvironment
import org.flaxo.core.env.file.EnvironmentFile
import org.flaxo.core.env.file.LocalEnvironmentFile
import java.nio.file.Paths

/**
 * C++ environment where all tests are bash io tests.
 */
internal class CppBashEnvironment : Environment by SimpleEnvironment(
        setOf(
                file(".travis.yml"),
                file("run_tests.sh"),
                file("run_test.sh"),
                file("src/main/cpp/main.cpp"),
                file("src/main/cpp/minus.cpp"),
                file("src/main/cpp/minus.h"),
                file("src/main/cpp/plus.cpp"),
                file("src/main/cpp/plus.h"),
                file("src/test/resources/minus/negative_result/input.txt"),
                file("src/test/resources/minus/negative_result/output.txt"),
                file("src/test/resources/plus/positive_numbers/input.txt"),
                file("src/test/resources/plus/positive_numbers/output.txt"),
                file("src/test/resources/plus/negative_numbers/input.txt"),
                file("src/test/resources/plus/negative_numbers/output.txt")
        )
)

private val bashEnvironmentDirectory = Paths.get("cpp/src/main/resources/bash_environment").toAbsolutePath()

private fun file(path: String): EnvironmentFile =
        LocalEnvironmentFile(
                localPath = bashEnvironmentDirectory.resolve(path),
                path = Paths.get(path)
        )
