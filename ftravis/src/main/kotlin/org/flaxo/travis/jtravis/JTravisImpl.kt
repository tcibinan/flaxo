package org.flaxo.travis.jtravis

import fr.inria.jtravis.JTravis
import io.vavr.control.Either
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.flaxo.travis.Travis
import org.flaxo.travis.TravisRepository
import org.flaxo.travis.TravisUser

/**
 * Travis implementation based on jTravis library.
 */
class JTravisImpl : Travis {

    val jTravis by lazy { JTravis.Builder().build() }

    override fun getUser(): Either<ResponseBody, TravisUser> {
        TODO("not implemented")
    }

    override fun getRepository(userName: String,
                               repositoryName: String
    ): Either<ResponseBody, TravisRepository> =
            jTravis.repository()
                    .fromSlug("$userName/$repositoryName")
                    .map { JTravisRepository(it) }
                    .map { Either.right<ResponseBody, TravisRepository>(it) }
                    .orElseGet {
                        Either.left(ResponseBody.create(
                                MediaType.parse("application/json"),
                                "repository not found"
                        ))
                    }


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
}