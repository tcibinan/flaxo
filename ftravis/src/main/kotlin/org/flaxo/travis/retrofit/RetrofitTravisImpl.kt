package org.flaxo.travis.retrofit

import io.vavr.control.Either
import okhttp3.ResponseBody
import org.flaxo.travis.Travis
import org.flaxo.travis.TravisBuild
import org.flaxo.travis.TravisBuildType
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
            getBuildsRecursive(userName, repositoryName, eventType)
                    .map { it.map { RetrofitTravisBuild(it) } }

    private fun getBuildsRecursive(userName: String,
                                   repositoryName: String,
                                   eventType: TravisBuildType,
                                   offset: Int = 0
    ): Either<ResponseBody, List<RetrofitTravisBuildPOJO>> =
            travisClient
                    .getBuilds(authorization(),
                            repositorySlug(userName, repositoryName),
                            eventType = eventType.apiParam,
                            offset = offset
                    )
                    .call()
                    .map { prevBuilds ->
                        prevBuilds.takeUnless { it.pagination.last }
                                ?.takeIf { it.pagination.next?.offset != null }
                                ?.let {
                                    val nextPageOffset = prevBuilds.pagination.next?.offset ?: 0
                                    getBuildsRecursive(userName, repositoryName, eventType, nextPageOffset)
                                            .takeIf { it.isRight }
                                            ?.let { prevBuilds.builds + it.get() }
                                }
                                ?: prevBuilds.builds
                    }

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
