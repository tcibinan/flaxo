package org.flaxo.travis.retrofit

import io.vavr.control.Either
import okhttp3.ResponseBody
import org.flaxo.travis.Travis
import org.flaxo.travis.TravisBuild
import org.flaxo.travis.TravisBuildType
import org.flaxo.travis.TravisClient
import org.flaxo.travis.TravisRepository
import org.flaxo.travis.TravisUser
import retrofit2.Call

/**
 * Travis client implementation class.
 */
class RetrofitTravisImpl(private val travisClient: TravisClient,
                         private val travisToken: String
) : Travis {

    override fun getUser(): Either<ResponseBody, TravisUser> =
            travisClient.getUser(authorization())
                    .call()
                    .map { RetrofitTravisUser(it) }

    override fun getRepository(userName: String,
                               repositoryName: String
    ): Either<ResponseBody, TravisRepository> =
            travisClient.getRepository(authorization(), repositorySlug(userName, repositoryName))
                    .call()
                    .map { RetrofitTravisRepository(it) }

    override fun activate(userName: String,
                          repositoryName: String
    ): Either<ResponseBody, TravisRepository> =
            travisClient.activate(authorization(), repositorySlug(userName, repositoryName))
                    .call()
                    .map { RetrofitTravisRepository(it) }

    override fun deactivate(userName: String,
                            repositoryName: String
    ): Either<ResponseBody, TravisRepository> =
            travisClient.deactivate(authorization(), repositorySlug(userName, repositoryName))
                    .call()
                    .map { RetrofitTravisRepository(it) }

    override fun sync(travisUserId: String): ResponseBody? =
            travisClient.sync(authorization(), travisUserId)
                    .callUnit()

    override fun getBuilds(userName: String,
                           repositoryName: String,
                           eventType: TravisBuildType
    ): Either<ResponseBody, List<TravisBuild>> =
            travisClient
                    .getBuilds(authorization(),
                            repositorySlug(userName, repositoryName),
                            eventType.apiParam
                    )
                    .call()
                    .map { it.builds.map { RetrofitTravisBuild(it) } }

    private fun authorization() = "token $travisToken"

    private fun repositorySlug(userName: String,
                               repositoryName: String
    ) =
            "$userName/$repositoryName"

    private fun <T> Call<T>.call(): Either<ResponseBody, T> =
            execute().run {
                if (isSuccessful) Either.right(body())
                else Either.left(errorBody())
            }

    private fun <T> Call<T>.callUnit(): ResponseBody? =
            execute().run { if (isSuccessful) null else errorBody() }

}
