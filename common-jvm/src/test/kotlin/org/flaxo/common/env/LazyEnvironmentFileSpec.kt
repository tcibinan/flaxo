package org.flaxo.common.env

import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.flaxo.common.deleteDirectoryRecursively
import org.flaxo.common.env.file.LazyLocalEnvironmentFile
import org.flaxo.common.env.file.LazyEnvironmentFile
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.nio.file.Files
import java.nio.file.Paths

object LazyEnvironmentFileSpec : Spek({
    val localDirectory = Files.createTempDirectory("lazy-environment-file-spec")
    val path = Paths.get("some/path/to/file")
    val content = "content"

    describe("lazy environment file") {

        afterGroup { deleteDirectoryRecursively(localDirectory) }

        on("getting content") {
            val file = LazyEnvironmentFile(path) { content.byteInputStream() }

            it("should return original content") {
                file.content shouldEqual content
            }
        }

        on("getting binary content") {
            val file = LazyEnvironmentFile(path) { content.byteInputStream() }

            it("should return original binary content") {
                file.binaryContent shouldEqual content.toByteArray()
            }
        }

        on("converting to local path") {
            val file = LazyEnvironmentFile(path) { content.byteInputStream() }
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
