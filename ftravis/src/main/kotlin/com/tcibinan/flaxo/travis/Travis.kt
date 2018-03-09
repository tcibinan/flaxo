package com.tcibinan.flaxo.travis

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
     * Activates builds on the [repositoryName].
     *
     * @param userName Git owner nickname.
     * @param repositoryName Git repository name to be activated.
     * @return Either activated travis repository or response body of the request to travis api.
     */
    fun activate(userName: String, repositoryName: String): Either<ResponseBody, TravisRepository>

    /**
     * Deactivates builds on the [repositoryName].
     *
     * @param userName Git owner nickname.
     * @param repositoryName Git repository name to be deactivated.
     * @return Either deactivated travis repository or response body of the request to travis api.
     */
    fun deactivate(userName: String, repositoryName: String): Either<ResponseBody, TravisRepository>
}

