package org.flaxo.travis.jtravis

import arrow.core.Either
import fr.inria.jtravis.JTravis
import okhttp3.ResponseBody
import org.flaxo.travis.Travis
import org.flaxo.travis.TravisBuild
import org.flaxo.travis.TravisBuildType
import org.flaxo.travis.TravisRepository
import org.flaxo.travis.TravisUser

/**
 * Travis implementation based on jTravis library.
 */
class JTravisImpl : Travis {

    private val jTravis: JTravis by lazy { JTravis.Builder().build() }

    override fun getUser(): Either<ResponseBody, TravisUser> {
        TODO("not implemented")
    }

    override fun getRepository(userName: String,
                               repositoryName: String
    ): Either<ResponseBody, TravisRepository> =
            jTravis.repository().fromSlug("$userName/$repositoryName")
                    .map { JTravisRepository(it) }
                    .orElse(null)
                    ?.let { Either.right(it) }
                    ?: Either.left(ResponseBody.create(null, "repository not found"))

    override fun activate(userName: String,
                          repositoryName: String
    ): Either<ResponseBody, TravisRepository> {
        TODO("not implemented")
    }

    override fun deactivate(userName: String,
                            repositoryName: String
    ): Either<ResponseBody, TravisRepository> {
        TODO("not implemented")
    }

    override fun sync(travisUserId: String): ResponseBody? {
        TODO("not implemented")
    }

    override fun getBuilds(userName: String,
                           repositoryName: String,
                           eventType: TravisBuildType
    ): Either<ResponseBody, List<TravisBuild>> {
        TODO("not implemented")
    }
}