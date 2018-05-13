package org.flaxo.travis

import io.vavr.control.Either
import okhttp3.ResponseBody

/**
 * Travis client interface.
 *
 * Client is associated with a single user.
 */
interface Travis {

    /**
     * Retrieves associated with the client user.
     *
     * @return Either associated travis user or response body of the request to travis api.
     */
    fun getUser(): Either<ResponseBody, TravisUser>

    /**
     * Retrieves travis repository by [repositoryName].
     *
     * @param userName Git owner nickname.
     * @param repositoryName Git repository name to be retrieved.
     * @return Either travis repository or response body
     * of the request to travis api if something went bad.
     */
    fun getRepository(userName: String,
                      repositoryName: String
    ): Either<ResponseBody, TravisRepository>

    /**
     * Activates builds on the repository by [repositoryName].
     *
     * @param userName Git owner nickname.
     * @param repositoryName Git repository name to be activated.
     * @return Either activated travis repository or response body
     * of the request to travis api if something went bad.
     */
    fun activate(userName: String,
                 repositoryName: String
    ): Either<ResponseBody, TravisRepository>

    /**
     * Deactivates builds on the repository by [repositoryName].
     *
     * @param userName Git owner nickname.
     * @param repositoryName Git repository name to be deactivated.
     * @return Either deactivated travis repository or response body
     * of the request to travis api if something went bad.
     */
    fun deactivate(userName: String,
                   repositoryName: String
    ): Either<ResponseBody, TravisRepository>

    /**
     * Triggers a sync on a user's travis account with user's github account.
     *
     * @param travisUserId Travis user id.
     * @return Null or response body of the request to travis api
     * if something went bad.
     */
    fun sync(travisUserId: String): ResponseBody?
}

