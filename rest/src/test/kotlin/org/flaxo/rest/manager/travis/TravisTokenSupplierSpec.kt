package org.flaxo.rest.manager.travis

import arrow.core.Either
import arrow.core.Try
import org.amshove.kluent.shouldNotBeBlank
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

object TravisTokenSupplierSpec: SubjectSpek<TravisTokenSupplier>({

    val githubUsername = System.getenv("GITHUB_USER1_NAME")
    val githubToken = System.getenv("GITHUB_USER1_TOKEN")

    subject { TravisTokenSupplier() }

    describe("travis service") {
        on("getting travis token") {

            val result: Either<Throwable, String> = Try { subject.supply(githubUsername, githubToken) }.toEither()

            it("should finish with zero code") {
                result.mapLeft {
                    throw AssertionError("Travis token retrieving failed due to: ${it.message}")
                }
            }

            it("should return non-empty travis token") {
                result.map { it.shouldNotBeBlank() }
            }
        }
    }

})
