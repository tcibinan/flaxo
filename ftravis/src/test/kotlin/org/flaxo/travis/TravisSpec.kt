package org.flaxo.travis

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import io.kotlintest.matchers.shouldBe
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import retrofit2.Call
import retrofit2.Response

object TravisSpec: SubjectSpek<Travis>({

    val githubUsername = "githubUsername"
    val travisToken = "travisToken"
    val repositoryName = "repositoryName"
    val repositorySlug = "$githubUsername/$repositoryName"
    val firstResponse = Response.success(TravisRepository().also { it.active = false })
    val secondResponse = Response.success(TravisRepository().also { it.active = true })
    val call = mock<Call<TravisRepository>> {
        on { execute() }.thenReturn(firstResponse, secondResponse)
    }
    val travisClient = mock<TravisClient> {
        on { activate(eq("token $travisToken"), eq(repositorySlug)) }.thenReturn(call)
        on { deactivate(eq("token $travisToken"), eq(repositorySlug)) }.thenReturn(call)
    }
    subject { SimpleTravis(travisClient, travisToken) }

    describe("travis wrapper") {

        on("deactivating a repository") {
            val repository = subject.deactivate(githubUsername, repositoryName)
                    .getOrElseThrow { errorBody ->
                        TravisException("Travis repository wasn't received due to: ${errorBody.string()}")
                    }

            it("should set repository to inactive status") {
                repository.active shouldBe false
            }
        }

        on("activating a repository") {
            val repository = subject.activate(githubUsername, repositoryName)
                    .getOrElseThrow { errorBody ->
                        TravisException("Travis user wasn't received due to: ${errorBody.string()}")
                    }

            it("should set repository to active status") {
                repository.active shouldBe true
            }
        }
    }
})