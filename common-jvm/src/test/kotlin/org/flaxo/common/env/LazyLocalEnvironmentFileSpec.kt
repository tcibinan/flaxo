package org.flaxo.common.env

import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.flaxo.common.deleteDirectoryRecursively
import org.flaxo.common.env.file.LazyLocalEnvironmentFile
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.nio.file.Files
import java.nio.file.Paths

object LazyLocalEnvironmentFileSpec : Spek({
    val relativePath = Paths.get("folder/subfolder/file.extension")
    val localDirectory = Files.createTempDirectory("tempDirectory")
    val localPath = localDirectory.resolve(relativePath)
    val content = "content"

    describe("lazy local environment file") {

        afterEachTest { Files.deleteIfExists(localPath) }
        afterGroup { deleteDirectoryRecursively(localDirectory) }

        on("initialization") {
            LazyLocalEnvironmentFile(relativePath, localDirectory) {content.byteInputStream()}

            it("should not create local file") {
                Files.exists(localPath).shouldNotBeNull()
            }
        }

        on("getting relative path") {
            val file = LazyLocalEnvironmentFile(relativePath, localDirectory) {content.byteInputStream()}
            val storedRelativePath = file.path

            it("should not create local file") {
                Files.exists(localPath).shouldNotBeNull()
            }

            it("should return original relative path") {
                storedRelativePath shouldEqual relativePath
            }
        }

        on("getting local path") {
            val file = LazyLocalEnvironmentFile(relativePath, localDirectory) {content.byteInputStream()}
            val generatedLocalPath = file.localPath

            it("should generate local path from the given local directory and relative file path") {
                generatedLocalPath shouldEqual localPath
            }

            it("should create file by the local path") {
                Files.exists(localPath).shouldBeTrue()
            }

            it("should create file with the given content") {
                Files.readAllLines(localPath).firstOrNull() shouldEqual content
            }
        }

        on("getting content") {
            val file = LazyLocalEnvironmentFile(relativePath, localDirectory) {content.byteInputStream()}
            file.content

            it("should create file by the local path") {
                Files.exists(localPath).shouldBeTrue()
            }

            it("should create file with the given content") {
                Files.readAllLines(localPath).firstOrNull() shouldEqual content
            }
        }

        on("getting binary content") {
            val file = LazyLocalEnvironmentFile(relativePath, localDirectory) {content.byteInputStream()}
            file.binaryContent

            it("should create file by the local path") {
                Files.exists(localPath).shouldBeTrue()
            }

            it("should create file with the given content") {
                Files.readAllLines(localPath).firstOrNull() shouldEqual content
            }
        }
    }
})
