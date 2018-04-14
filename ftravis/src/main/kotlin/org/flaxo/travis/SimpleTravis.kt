package org.flaxo.travis

import io.vavr.control.Either
import okhttp3.ResponseBody

/**
 * Travis client implementation class.
 */
class SimpleTravis(private val travisClient: TravisClient,
                   private val travisToken: String
) : Travis {

    override fun getUser(): Either<ResponseBody, TravisUser> =
            travisClient.getUser(authorization()).execute()
                    .run {
                        if (isSuccessful) Either.right(body())
                        else Either.left(errorBody())
                    }

    override fun activate(userName: String, repositoryName: String): Either<ResponseBody, TravisRepository> =
            travisClient.activate(authorization(), repositorySlug(userName, repositoryName)).execute()
                    .run {
                        if (isSuccessful) Either.right(body())
                        else Either.left(errorBody())
                    }

    override fun deactivate(userName: String, repositoryName: String): Either<ResponseBody, TravisRepository> =
            travisClient.deactivate(authorization(), repositorySlug(userName, repositoryName)).execute()
                    .run {
                        if (isSuccessful) Either.right(body())
                        else Either.left(errorBody())
                    }

    override fun sync(travisUserId: String): ResponseBody? =
            travisClient.sync(authorization(), travisUserId).execute()
                    .run {
                        if (isSuccessful) null
                        else errorBody()
                    }

    private fun authorization() = "token $travisToken"

    private fun repositorySlug(userName: String, repositoryName: String) =
            "$userName/$repositoryName"

}