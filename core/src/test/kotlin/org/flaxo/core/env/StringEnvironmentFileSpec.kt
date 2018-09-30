package org.flaxo.core.env

import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.flaxo.core.deleteDirectoryRecursively
import org.flaxo.core.env.file.LazyLocalEnvironmentFile
import org.flaxo.core.env.file.StringEnvironmentFile
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.nio.file.Files
import java.nio.file.Paths

object StringEnvironmentFileSpec : Spek({
    val localDirectory = Files.createTempDirectory("local-environment-file-spec")
    val path = Paths.get("some/path/to/file")
    val content = "line1\nline2"

    describe("string environment file") {

        afterGroup { deleteDirectoryRecursively(localDirectory) }

        on("getting content") {
            val file = StringEnvironmentFile(path, content)

            it("should return original content") {
                file.content shouldEqual content
            }
        }

        on("getting binary content") {
            val file = StringEnvironmentFile(path, content)

            it("should return original binary content") {
                file.binaryContent shouldEqual content.toByteArray()
            }
        }

        on("converting to local path") {
            val file = StringEnvironmentFile(path, content)
            val localFile = file.toLocalFile(localDirectory)

            it("should return an instance of LazyLocalEnvironmentFile") {
                (localFile is LazyLocalEnvironmentFile).shouldBeTrue()
            }

            it("should return a file with the resolved local path") {
                localFile.localPath shouldEqual localDirectory.resolve(path)
            }

            it("should return a file with the original path") {
                localFile.path shouldEqual path
            }

            it("should return a file with the original content") {
                localFile.content shouldEqual content
            }

            it("should return a file with the original binary content") {
                localFile.binaryContent shouldEqual content.toByteArray()
            }
        }
    }
})