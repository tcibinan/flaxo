package org.flaxo.common.env

import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotThrow
import org.amshove.kluent.shouldThrow
import org.flaxo.common.env.file.LocalEnvironmentFile
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

object LocalEnvironmentFileSpec : Spek({
    val content = "content"
    val nonExistingPath: Path = Paths.get("/non/existing/path")
    val existingPath = Files.createTempFile("local-environment-file", "spec")
    Files.copy(content.byteInputStream(), existingPath, StandardCopyOption.REPLACE_EXISTING)

    describe("simple environment file based on non-existing path") {

        on("initialization") {
            Files.deleteIfExists(nonExistingPath)

            it("should not fail") {
                { LocalEnvironmentFile(nonExistingPath) } shouldNotThrow java.nio.file.NoSuchFileException::class
            }

        }

        on("getting content") {
            Files.deleteIfExists(nonExistingPath)
            val file = LocalEnvironmentFile(nonExistingPath)

            it("should fail") {
                { file.content } shouldThrow java.nio.file.NoSuchFileException::class
            }
        }

        on("getting binary content") {
            Files.deleteIfExists(nonExistingPath)
            val file = LocalEnvironmentFile(nonExistingPath)

            it("should fail") {
                { file.binaryContent } shouldThrow java.nio.file.NoSuchFileException::class
            }
        }
    }

    describe("simple environment file based on existing path") {

        on("getting content") {
            val file = LocalEnvironmentFile(existingPath)

            it("should return underlying file content") {
                file.content shouldEqual content
            }
        }

        on("getting binary content") {
            val file = LocalEnvironmentFile(existingPath)

            it("should return underlying file binary content") {
                file.binaryContent shouldEqual content.toByteArray()
            }
        }
    }
})
