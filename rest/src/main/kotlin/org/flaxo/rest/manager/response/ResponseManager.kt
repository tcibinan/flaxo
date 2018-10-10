package org.flaxo.rest.manager.response

/**
 * Http response manager.
 *
 * It is just a handy suite that forms a http response entity
 * with the given body and appropriate http response status code.
 */
interface ResponseManager {

    /**
     * Forms a response with status code 200.
     */
    fun <T> ok(body: T? = null): Response<T>

    /**
     * Forms a response with status code 400.
     */
    fun <T> bad(body: T? = null): Response<T>

    /**
     * Forms a response with status code 400.
     */
    fun <T> bad(message: String): Response<T>

    /**
     * Forms a response with status code 404.
     */
    fun <T> notFound(body: T? = null): Response<T>

    /**
     * Forms a response with status code 404.
     */
    fun <T> notFound(message: String): Response<T>

    /**
     * Forms a response with status code 500.
     */
    fun <T> serverError(body: T? = null): Response<T>

    /**
     * Forms a response with status code 401.
     */
    fun <T> unauthorized(body: T? = null): Response<T>

    /**
     * Forms a response with status code 403.
     */
    fun <T> forbidden(body: T? = null): Response<T>

    /**
     * Forms a response with status code 404 and course not found message.
     */
    fun <T> courseNotFound(username: String, courseName: String): Response<T>

    /**
     * Forms a response with status code 404 and user not found message.
     */
    fun <T> userNotFound(username: String): Response<T>

    /**
     * Forms a response with status code 400 and github token absences message.
     */
    fun <T> githubTokenNotFound(username: String): Response<T>

    /**
     * Forms a response with status code 404 and github id not found message.
     */
    fun <T> githubIdNotFound(username: String): Response<T>

    /**
     * Forms a response with status code 404 and course task not found message.
     */
    fun <T> taskNotFound(username: String, courseName: String, taskBranch: String): Response<T>
}
