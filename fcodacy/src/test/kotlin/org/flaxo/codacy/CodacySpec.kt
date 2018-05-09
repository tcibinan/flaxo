package org.flaxo.codacy

import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.flaxo.codacy.model.CommitDetails
import org.flaxo.codacy.response.CommitDetailsResponse
import org.flaxo.fretrofit.retrofitMock

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
                response.isRight.shouldBeTrue()
            }

            it("should contain commit grade") {
                response.get().commit.grade shouldEqual grade
            }
        }

        on("creating project") {
            val response = subject.createProject(projectName, repositoryUrl)

            it("should not contain response body") {
                response.shouldBeNull()
            }
        }

        on("deleting project") {
            val response = subject.deleteProject(projectName)

            it("should not contain response body") {
                response.shouldBeNull()
            }
        }
    }
})