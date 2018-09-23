package org.flaxo.frontend.client

import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.Deferred
import org.flaxo.common.*
import org.flaxo.frontend.Credentials
import org.flaxo.common.interop.courseFromDynamic
import org.flaxo.common.interop.courseStatisticsFromDynamic
import org.flaxo.common.interop.githubAuthDataFromDynamic
import org.flaxo.common.interop.languageFromDynamic
import org.flaxo.common.interop.userFromDynamic
import org.flaxo.frontend.wrapper.btoa
import org.flaxo.frontend.wrapper.encodeURIComponent
import org.w3c.xhr.XMLHttpRequest

class PlainHttpFlaxoClient(private val baseUrl: String) : FlaxoClient {

    override suspend fun registerUser(credentials: Credentials): User =
            handleErrors("User registering failed.") {
                val request = post("/user/register",
                        parameters = mapOf("nickname" to credentials.username, "password" to credentials.password)
                ).await()
                if (request.status.toInt() == 200) {
                    return userFromDynamic(JSON.parse<Payload<dynamic>>(request.responseText).payload)
                } else {
                    onError(request)
                }
            }

    override suspend fun getSelf(credentials: Credentials): User =
            handleErrors("Current user retrieving failed.") {
                val request = get("/user", credentials = credentials).await()
                if (request.status.toInt() == 200) {
                    return userFromDynamic(JSON.parse<Payload<dynamic>>(request.responseText).payload)
                } else {
                    onError(request)
                }
            }

    override suspend fun getUserCourses(credentials: Credentials,
                                        username: String
    ): List<Course> = handleErrors("User courses retrieving failed.") {
        val request = get("/course/all",
                parameters = mapOf("nickname" to username),
                credentials = credentials
        ).await()
        if (request.status.toInt() == 200) {
            return JSON.parse<Payload<Array<dynamic>>>(request.responseText)
                    .payload
                    ?.toList()
                    ?.map { courseFromDynamic(it) }
                    ?: throw FlaxoHttpException("There is no courses in server response")
        } else {
            onError(request)
        }
    }

    override suspend fun createCourse(credentials: Credentials,
                                      courseName: String,
                                      description: String?,
                                      language: String,
                                      testingLanguage: String,
                                      testingFramework: String,
                                      numberOfTasks: Int
    ): Course = handleErrors("Course creation handleErrors failed.") {
        val request = post("/course/create",
                parameters = mapOf("courseName" to courseName,
                        "description" to description,
                        "language" to language,
                        "testingLanguage" to testingLanguage,
                        "testingFramework" to testingFramework,
                        "numberOfTasks" to numberOfTasks),
                credentials = credentials
        ).await()
        if (request.status.toInt() == 200) {
            return courseFromDynamic(JSON.parse<Payload<dynamic>>(request.responseText).payload)
        } else {
            onError(request)
        }
    }

    override suspend fun getAvailableLanguages(): List<Language> =
            handleErrors("Available languages retrieving failed.") {
                val request = get("/settings/languages").await()
                if (request.status.toInt() == 200) {
                    return JSON.parse<Payload<Array<dynamic>>>(request.responseText)
                            .payload
                            ?.toList()
                            ?.map { languageFromDynamic(it) }
                            ?: emptyList()
                } else {
                    onError(request)
                }
            }

    override suspend fun getCourseStatistics(credentials: Credentials,
                                             username: String,
                                             courseName: String
    ): CourseStatistics = handleErrors("Course statistics retrieving failed.") {
        val request = get("/statistics",
                parameters = mapOf("owner" to username, "course" to courseName),
                credentials = credentials
        ).await()
        if (request.status.toInt() == 200) {
            return courseStatisticsFromDynamic(JSON.parse<Payload<dynamic>>(request.responseText).payload)
        } else {
            onError(request)
        }
    }

    override suspend fun startCourse(credentials: Credentials,
                                     courseName: String
    ): Course = handleErrors("Course starting failed.") {
        val request = post("/course/activate",
                parameters = mapOf("courseName" to courseName),
                credentials = credentials
        ).await()
        if (request.status.toInt() == 200) {
            return courseFromDynamic(JSON.parse<Payload<dynamic>>(request.responseText).payload)
        } else {
            onError(request)
        }
    }

    override suspend fun deleteCourse(credentials: Credentials, courseName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun analysePlagiarism(credentials: Credentials,
                                           courseName: String
    ) = handleErrors("Plagiarism analysis scheduling failed.") {
        val request = post("/course/analyse/plagiarism",
                parameters = mapOf("courseName" to courseName),
                credentials = credentials
        ).await()
        if (request.status.toInt() != 200) {
            onError(request)
        }
    }

    override suspend fun syncCourse(credentials: Credentials,
                                    courseName: String
    ) = handleErrors("Course synchronization failed.") {
        val request = post("/course/sync",
                parameters = mapOf("courseName" to courseName),
                credentials = credentials
        ).await()
        if (request.status.toInt() != 200) {
            onError(request)
        }
    }

    override suspend fun updateRules(credentials: Credentials,
                                     courseName: String,
                                     task: String,
                                     deadline: String?
    ) = handleErrors("Task rules updating failed.") {
        val request = post("/task/update/rules",
                parameters = mapOf("courseName" to courseName,
                        "taskBranch" to task,
                        "deadline" to deadline),
                credentials = credentials
        ).await()
        if (request.status.toInt() != 200) {
            onError(request)
        }
    }

    override suspend fun updateScores(credentials: Credentials,
                                      courseName: String,
                                      task: String,
                                      scores: Map<String, Int>
    ) = handleErrors("Task scores updating failed.") {
        val request = post("/task/update/scores",
                parameters = mapOf("courseName" to courseName,
                        "taskBranch" to task),
                body = scores.map { (a, b) -> "\"$a\": $b" }
                        .joinToString(", ", "{", "}")
                        .let { JSON.parse<Map<String, Int>>(it) },
                credentials = credentials
        ).await()
        if (request.status.toInt() != 200) {
            onError(request)
        }
    }

    override suspend fun addCodacyToken(credentials: Credentials,
                                        codacyToken: String
    ) = handleErrors("Codacy token addition failed.") {
        val request = put("/codacy/token",
                parameters = mapOf("token" to codacyToken),
                credentials = credentials
        ).await()
        if (request.status.toInt() != 200) {
            onError(request)
        }
    }

    override suspend fun activateCodacy(credentials: Credentials,
                                        courseName: String
    ) = handleErrors("Activating codacy for course failed.") {
        val request = post("/course/activate/codacy",
                parameters = mapOf("courseName" to courseName),
                credentials = credentials
        ).await()
        if (request.status.toInt() != 200) {
            onError(request)
        }
    }

    override suspend fun activateTravis(credentials: Credentials,
                                        courseName: String
    ) = handleErrors("Activating travis for course failed.") {
        val request = post("/course/activate/travis",
                parameters = mapOf("courseName" to courseName),
                credentials = credentials
        ).await()
        if (request.status.toInt() != 200) {
            onError(request)
        }
    }

    override suspend fun downloadStatistics(credentials: Credentials,
                                            courseName: String,
                                            format: String
    ): dynamic = handleErrors("Course statistics retrieving failed.") {
        val request = get("/statistics/download",
                parameters = mapOf("courseName" to courseName, "format" to format),
                credentials = credentials
        ).await()
        if (request.status.toInt() == 200) {
            return request.response
        } else {
            onError(request)
        }
    }

    override suspend fun getGithubAuthData(credentials: Credentials): GithubAuthData =
            handleErrors("Course statistics retrieving failed.") {
                val request = get("/github/auth", credentials = credentials).await()
                if (request.status.toInt() == 200) {
                    return githubAuthDataFromDynamic(JSON.parse<Payload<dynamic>>(request.responseText).payload)
                } else {
                    onError(request)
                }
            }

    private inline fun <T> handleErrors(errorMessage: String, function: () -> T): T =
            try {
                function()
            } catch (e: FlaxoHttpException) {
                throw FlaxoHttpException(errorMessage, e, e.userMessage)
            } catch (e: Throwable) {
                throw FlaxoHttpException(errorMessage, e)
            }

    private fun onError(request: XMLHttpRequest): Nothing {
        val errorPayload: String?
        try {
            errorPayload = JSON.parse<Payload<String>>(request.responseText).payload
        } catch (e: Throwable) {
            throw FlaxoHttpException(request.responseText)
        }
        throw FlaxoHttpException(userMessage = errorPayload)
    }

    private fun get(method: String,
                    parameters: Map<String, Any?> = emptyMap(),
                    body: Any? = null,
                    credentials: Credentials? = null
    ): Deferred<XMLHttpRequest> = httpCall("GET", method, parameters, body, credentials)

    private fun post(method: String,
                     parameters: Map<String, Any?> = emptyMap(),
                     body: Any? = null,
                     credentials: Credentials? = null
    ): Deferred<XMLHttpRequest> = httpCall("POST", method, parameters, body, credentials)

    private fun put(method: String,
                    parameters: Map<String, Any?> = emptyMap(),
                    body: Any? = null,
                    credentials: Credentials? = null
    ): Deferred<XMLHttpRequest> = httpCall("PUT", method, parameters, body, credentials)

    private fun httpCall(httpMethod: String,
                         apiMethod: String,
                         parameters: Map<String, Any?>,
                         body: Any?,
                         credentials: Credentials?
    ): Deferred<XMLHttpRequest> = escapedHttpCall(httpMethod, apiMethod, parameters, body, credentials)

    private fun escapedHttpCall(httpMethod: String,
                                apiMethod: String,
                                parameters: Map<String, Any?>,
                                body: Any?,
                                credentials: Credentials?
    ): Deferred<XMLHttpRequest> = XMLHttpRequest()
            .apply {
                if (parameters.isEmpty()) {
                    open(httpMethod, "$baseUrl$apiMethod", async = true)
                } else {
                    val parametersString = parameters.filterValues { it != null }
                            .map { (key, value) -> "$key=${encodeURIComponent(value.toString())}" }
                            .joinToString("&")
                    open(httpMethod, "$baseUrl$apiMethod?$parametersString", async = true)
                }
                if (credentials != null) {
                    setRequestHeader("Authorization", authorizationToken(credentials))
                }
                if (body != null) {
                    setRequestHeader("Content-Type", "application/json")
                    send(JSON.stringify(body))
                } else {
                    send()
                }
            }
            .deferredDoneRequest()

    private fun XMLHttpRequest.deferredDoneRequest(): Deferred<XMLHttpRequest> {
        val deferred = CompletableDeferred<XMLHttpRequest>()
        onreadystatechange = {
            if (readyState == XMLHttpRequest.DONE) {
                deferred.complete(this)
            }
        }
        return deferred
    }

    private fun authorizationToken(credentials: Credentials) =
            "Basic " + btoa(credentials.username + ":" + credentials.password)

}
