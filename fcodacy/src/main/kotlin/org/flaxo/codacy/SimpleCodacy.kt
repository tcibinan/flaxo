package org.flaxo.codacy

import io.vavr.control.Either
import okhttp3.ResponseBody
import org.flaxo.codacy.request.ProjectRequest
import org.flaxo.codacy.response.CommitDetailsResponse
import retrofit2.Call

/**
 * Codacy client implementation.
 */
class SimpleCodacy(private val username: String,
                   private val codacyToken: String,
                   private val client: CodacyClient
) : Codacy {

    override fun commitDetails(projectName: String, commitId: String)
            : Either<ResponseBody, CommitDetailsResponse> =
            client.commitDetails(username, projectName, commitId, codacyToken)
                    .call()

    override fun createProject(projectName: String, repositoryUrl: String)
            : ResponseBody? =
            client.createPublicProject(ProjectRequest(projectName, repositoryUrl), codacyToken)
                    .callIgnoredBody()

    override fun deleteProject(projectName: String)
            : ResponseBody? =
            client.deleteProject(username, projectName, codacyToken)
                    .callIgnoredBody()

    private fun <T> Call<T>.call(): Either<ResponseBody, T> =
            execute().run {
                if (isSuccessful) Either.right(body())
                else Either.left(errorBody())
            }

    private fun <T> Call<T>.callIgnoredBody(): ResponseBody? =
            execute().run {
                if (isSuccessful) null
                else errorBody()
            }
}
