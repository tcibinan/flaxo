package org.flaxo.cpp

import org.flaxo.common.env.Environment
import org.flaxo.common.env.SimpleEnvironment
import org.flaxo.common.env.file.ClasspathEnvironmentFile
import org.flaxo.common.env.file.EnvironmentFile
import org.flaxo.common.env.file.LocalEnvironmentFile
import org.flaxo.common.env.file.StringEnvironmentFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * C++ environment where all tests are bash io tests.
 */
internal class CppBashEnvironment(travisWebHookUrl: String) : Environment by SimpleEnvironment(
        setOf(
                travisYmlFile(travisWebHookUrl),
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
) {
    companion object {
        private val environmentDirectory = Paths.get("src/main/resources/bash_environment").toAbsolutePath()
        private val alternativeEnvironmentDirectory = Paths.get("cpp/src/main/resources/bash_environment").toAbsolutePath()

        private fun travisYmlFile(travisWebHookUrl: String): EnvironmentFile =
                with(file(".travis.yml")) {
                    StringEnvironmentFile(path, content.replace("{{flaxo.travis.hook.url}}", travisWebHookUrl))
                }

        private fun file(path: String): EnvironmentFile = file(Paths.get(path))

        private fun file(path: Path): EnvironmentFile =
                localFile(path, environmentDirectory)
                        ?: localFile(path, alternativeEnvironmentDirectory)
                        ?: bundledFile(path)

        private fun localFile(path: Path, directory: Path): EnvironmentFile? =
                localPath(path, directory)
                        .takeIf { Files.isRegularFile(it) }
                        ?.let { LocalEnvironmentFile(localPath = it, path = path) }

        private fun localPath(path: Path, directory: Path) = directory.resolve(path)

        private fun bundledFile(path: Path) = ClasspathEnvironmentFile(classpathPath = bundlePath(path), path = path)

        private fun bundlePath(path: Path) = Paths.get("bundle/bash_environment").resolve(path)
    }
}
