package org.flaxo.rest.manager.response

import org.springframework.http.HttpStatus

/**
 * Response manager implementation.
 */
class SimpleResponseManager : ResponseManager {

    override fun <T> userNotFound(username: String): Response<T> =
            notFound("User $username not found")

    override fun <T> courseNotFound(username: String, courseName: String): Response<T> =
            notFound("Course $courseName wasn't found for user $username.")

    override fun <T> taskNotFound(username: String, courseName: String, taskBranch: String): Response<T> =
            notFound("Task $taskBranch wasn't found for $username/$courseName")

    override fun <T> githubTokenNotFound(username: String): Response<T> =
            bad("There is no github auth for $username")

    override fun <T> githubIdNotFound(username: String): Response<T> =
            notFound("Github id for $username is not set.")

    override fun <T> ok(body: T?): Response<T> = response(body, HttpStatus.OK)

    override fun <T> bad(body: T?): Response<T> = response(body, HttpStatus.BAD_REQUEST)

    override fun <T> bad(message: String): Response<T> = response(message, HttpStatus.BAD_REQUEST)

    override fun <T> notFound(body: T?): Response<T> = response(body, HttpStatus.NOT_FOUND)

    override fun <T> notFound(message: String): Response<T> = response(message, HttpStatus.NOT_FOUND)

    override fun <T> serverError(body: T?): Response<T> = response(body, HttpStatus.INTERNAL_SERVER_ERROR)

    override fun <T> unauthorized(body: T?): Response<T> = response(body, HttpStatus.UNAUTHORIZED)

    override fun <T> forbidden(body: T?): Response<T> = response(body, HttpStatus.FORBIDDEN)

    private fun <T> response(body: T?, httpStatus: HttpStatus): Response<T> = Response(body, httpStatus)

    private fun <T> response(message: String, httpStatus: HttpStatus): Response<T> = Response(message, httpStatus)

}
