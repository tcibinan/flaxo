package org.flaxo.rest.service.travis

import com.nhaarman.mockito_kotlin.mock
import io.vavr.kotlin.Try
import org.flaxo.model.DataService
import org.flaxo.travis.TravisClient
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertFalse

object TravisServiceSpec : SubjectSpek<TravisService>({

    val githubUsername = System.getenv("GITHUB_USER1_NAME")
    val githubToken = System.getenv("GITHUB_USER1_TOKEN")

    val travisClient = mock<TravisClient> { }
    val dataService = mock<DataService> { }

    subject { TravisSimpleService(travisClient, dataService) }

    describe("travis service") {
        on("getting travis token") {

            val result = Try {
                subject.retrieveTravisToken(githubUsername, githubToken)
            }

            it("should finish with zero code") {
                assert(result.isSuccess) {
                    "Travis token retrieving failed due to: ${result.cause.message}"
                }
            }

            it("should return non-empty travis token") {
                assertFalse { result.get().isBlank() }
            }
        }
    }

})