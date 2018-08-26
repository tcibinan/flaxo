package org.flaxo.rest.manager.response

import org.flaxo.common.Payload
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * Response manager implementation.
 */
class SimpleResponseManager : ResponseManager {

    override fun userNotFound(username: String): ResponseEntity<Any> =
            notFound("User $username not found")

    override fun courseNotFound(username: String,
                                courseName: String
    ): ResponseEntity<Any> =
            notFound("Course $courseName wasn't found for user $username.")

    override fun taskNotFound(username: String,
                              courseName: String,
                              taskBranch: String
    ): ResponseEntity<Any> =
            notFound("Task $taskBranch wasn't found for $username/$courseName")

    override fun githubTokenNotFound(username: String): ResponseEntity<Any> =
            bad("There is no github auth for $username")

    override fun githubIdNotFound(username: String): ResponseEntity<Any> =
            notFound("Github id for $username is not set.")

    override fun ok(body: Any?): ResponseEntity<Any> =
            response(body, HttpStatus.OK)

    override fun bad(body: Any?): ResponseEntity<Any> =
            response(body, HttpStatus.BAD_REQUEST)

    override fun notFound(body: Any?): ResponseEntity<Any> =
            response(body, HttpStatus.NOT_FOUND)

    override fun serverError(body: Any?): ResponseEntity<Any> =
            response(body, HttpStatus.INTERNAL_SERVER_ERROR)

    override fun unauthorized(body: Any?): ResponseEntity<Any> =
            response(body, HttpStatus.UNAUTHORIZED)

    override fun forbidden(body: Any?): ResponseEntity<Any> =
            response(body, HttpStatus.FORBIDDEN)

    private fun response(body: Any?, httpStatus: HttpStatus): ResponseEntity<Any> =
            body?.let { ResponseEntity<Any>(Payload(body), httpStatus) }
                    ?: ResponseEntity(httpStatus)

}
