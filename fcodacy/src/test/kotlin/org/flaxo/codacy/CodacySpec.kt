package org.flaxo.codacy

import io.kotlintest.matchers.shouldBe
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.flaxo.codacy.model.CommitDetails
import org.flaxo.codacy.response.CommitDetailsResponse
import org.flaxo.fretrofit.retrofitMock
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertNull
import kotlin.test.assertTrue

object CodacySpec : SubjectSpek<Codacy>({

    val apiToken = "token"
    val username = "username"
    val projectName = "projectName"
    val commitUUID = "commitUUID"
    val repositoryUrl = "repositoryUrl"
    val grade = "B"

    val retrofit = retrofitMock {
        get("/.*/.*/commit/.*") {
            body(CommitDetailsResponse(commit = CommitDetails(grade = grade)))
        }

        post("/project/create/public")

        post("/.*/.*/delete")
    }

    val codacyClient = retrofit.create(CodacyClient::class.java)

    subject { SimpleCodacy(username, apiToken, codacyClient) }

    describe("codacy") {

        on("getting commit details") {
            val response = subject.commitDetails(projectName, commitUUID)

            it("should successfully returns result") {
                assertTrue { response.isRight }
            }

            it("should contain commit grade") {
                response.get().commit.grade shouldBe grade
            }
        }

        on("creating project") {
            val response = subject.createProject(projectName, repositoryUrl)

            it("should not contain response body") {
                assertNull(response)
            }
        }

        on("deleting project") {
            val response = subject.deleteProject(projectName)

            it("should not contain response body") {
                assertNull(response)
            }
        }
    }
})