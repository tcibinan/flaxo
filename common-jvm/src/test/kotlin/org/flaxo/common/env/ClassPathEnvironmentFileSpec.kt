package org.flaxo.common.env

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.flaxo.common.NotFoundException
import org.flaxo.common.env.file.ClasspathEnvironmentFile
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.nio.file.Paths

object ClassPathEnvironmentFileSpec : Spek({
    val nonExistingPath = Paths.get("non-existing-file")
    val existingPath = Paths.get("bundle/file.txt")
    val content = "content\n"

    describe("bundled environment file") {
        on("initialization") {
            it("should not fail if file doesn't exist on classpath") {
                ClasspathEnvironmentFile(nonExistingPath)
            }
        }

        on("getting existing file content") {
            val file = ClasspathEnvironmentFile(existingPath)

            it("should return its content") {
                file.content shouldBeEqualTo content
            }
        }

        on("getting non-existing file content") {
            val file = ClasspathEnvironmentFile(nonExistingPath)

            it("should return its content") {
                { file.content } shouldThrow NotFoundException::class
            }
        }
    }
})
