package org.flaxo.travis

import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import okhttp3.ResponseBody
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.flaxo.travis.retrofit.RetrofitTravisImpl
import org.flaxo.travis.retrofit.RetrofitTravisRepositoryPOJO
import org.flaxo.travis.retrofit.TravisClient
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import retrofit2.Call
import retrofit2.Response

object RetrofitTravisSpec: SubjectSpek<Travis>({

    val githubUsername = "githubUsername"
    val travisToken = "travisToken"
    val travisAuthorization = "token $travisToken"
    val nonExistingRepositoryName = "nonExistingRepositoryName"
    val nonExistingRepositorySlug = "$githubUsername/$nonExistingRepositoryName"
    val repositoryName = "repositoryName"
    val repositorySlug = "$githubUsername/$repositoryName"
    val repositoryNonFoundResponse = Response.error<RetrofitTravisRepositoryPOJO>(404,
            ResponseBody.create(null, "repository not found"))
    val repositoryResponse = Response.success(RetrofitTravisRepositoryPOJO())
    val deactivatedRepositoryResponse = Response.success(RetrofitTravisRepositoryPOJO().also { it.active = false })
    val activatedRepositoryResponse = Response.success(RetrofitTravisRepositoryPOJO().also { it.active = true })
    val call = mock<Call<RetrofitTravisRepositoryPOJO>> {
        on { execute() }.thenReturn(
                repositoryNonFoundResponse,
                repositoryResponse,
                deactivatedRepositoryResponse,
                activatedRepositoryResponse
        )
    }
    val travisClient = mock<TravisClient> {
        on { getRepository(eq(travisAuthorization), eq(nonExistingRepositorySlug)) }.thenReturn(call)
        on { getRepository(eq(travisAuthorization), eq(repositorySlug)) }.thenReturn(call)
        on { activate(eq(travisAuthorization), eq(repositorySlug)) }.thenReturn(call)
        on { deactivate(eq(travisAuthorization), eq(repositorySlug)) }.thenReturn(call)
    }

    subject { RetrofitTravisImpl(travisClient, travisToken) }

    describe("retrofit travis") {

        on("getting non-existing travis repository") {
            val eitherRepository = subject.getRepository(githubUsername, nonExistingRepositoryName)

            it("should throw return errorBody") {
                eitherRepository.isLeft.shouldBeTrue()
            }
        }

        on("getting travis repository") {
            val eitherRepository = subject.getRepository(githubUsername, repositoryName)

            it("should return travis repository instance") {
                eitherRepository.getOrElseThrow { errorBody ->
                    throw TravisException(errorBody.string())
                }
            }
        }

        on("deactivating a repository") {
            val repository = subject.deactivate(githubUsername, repositoryName)
                    .getOrElseThrow { errorBody ->
                        TravisException("Travis repository wasn't received due to: ${errorBody.string()}")
                    }

            it("should set repository to inactive status") {
                repository.active.shouldBeFalse()
            }
        }

        on("activating a repository") {
            val repository = subject.activate(githubUsername, repositoryName)
                    .getOrElseThrow { errorBody ->
                        TravisException("Travis user wasn't received due to: ${errorBody.string()}")
                    }

            it("should set repository to active status") {
                repository.active.shouldBeTrue()
            }
        }
    }
})