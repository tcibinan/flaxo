package org.flaxo.travis

import io.vavr.control.Either
import okhttp3.ResponseBody
import retrofit2.Call

/**
 * Travis client implementation class.
 */
class SimpleTravis(private val travisClient: TravisClient,
                   private val travisToken: String
) : Travis {

    override fun getUser(): Either<ResponseBody, TravisUser> =
            travisClient.getUser(authorization())
                    .call()

    override fun activate(userName: String, repositoryName: String): Either<ResponseBody, TravisRepository> =
            travisClient.activate(authorization(), repositorySlug(userName, repositoryName))
                    .call()

    override fun deactivate(userName: String, repositoryName: String): Either<ResponseBody, TravisRepository> =
            travisClient.deactivate(authorization(), repositorySlug(userName, repositoryName))
                    .call()

    override fun sync(travisUserId: String): ResponseBody? =
            travisClient.sync(authorization(), travisUserId)
                    .callUnit()

    private fun authorization() = "token $travisToken"

    private fun repositorySlug(userName: String, repositoryName: String) =
            "$userName/$repositoryName"

    private fun <T> Call<T>.call(): Either<ResponseBody, T> =
            execute().run {
                if (isSuccessful) Either.right(body())
                else Either.left(errorBody())
            }

    private fun <T> Call<T>.callUnit(): ResponseBody? =
            execute().run { if (isSuccessful) null else errorBody() }

}
