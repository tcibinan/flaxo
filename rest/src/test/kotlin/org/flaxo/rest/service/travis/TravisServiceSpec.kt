package org.flaxo.rest.service.travis

import arrow.core.Either
import arrow.core.Try
import com.nhaarman.mockito_kotlin.mock
import org.amshove.kluent.shouldNotBeBlank
import org.flaxo.model.DataService
import org.flaxo.rest.service.git.GitService
import org.flaxo.travis.retrofit.TravisClient
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

object TravisServiceSpec : SubjectSpek<TravisService>({

    val githubUsername = System.getenv("GITHUB_USER1_NAME")
    val githubToken = System.getenv("GITHUB_USER1_TOKEN")

    val travisClient = mock<TravisClient> { }
    val dataService = mock<DataService> { }
    val gitService = mock<GitService> { }

    subject { SimpleTravisService(travisClient, dataService, gitService) }

    describe("travis service") {
        on("getting travis token") {

            val result: Either<Throwable, String> = Try {
                subject.retrieveTravisToken(githubUsername, githubToken)
            }.toEither()

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