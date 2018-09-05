package org.flaxo.frontend.client

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

    override fun registerUser(credentials: Credentials): User {
        try {
            val request = post("/user/register",
                    parameters = mapOf("nickname" to credentials.username, "password" to credentials.password))
            if (request.status.toInt() == 200) {
                return userFromDynamic(JSON.parse<Payload<dynamic>>(request.responseText).payload)
            } else {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("User registering failed.", e)
        }
    }

    override fun getSelf(credentials: Credentials): User {
        try {
            val request = get("/user", credentials = credentials)
            if (request.status.toInt() == 200) {
                return userFromDynamic(JSON.parse<Payload<dynamic>>(request.responseText).payload)
            } else {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Current user retrieving failed.", e)
        }
    }

    override fun getUserCourses(credentials: Credentials, username: String): List<Course> {
        try {
            val request = get("/course/all",
                    parameters = mapOf("nickname" to username),
                    credentials = credentials)
            if (request.status.toInt() == 200) {
                return JSON.parse<Payload<Array<dynamic>>>(request.responseText)
                        .payload
                        ?.toList()
                        ?.map { courseFromDynamic(it) }
                        ?: throw FlaxoHttpCallException("There is no courses in server response")
            } else {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("User courses retrieving failed.", e)
        }
    }

    override fun createCourse(credentials: Credentials,
                              courseName: String,
                              description: String?,
                              language: String,
                              testingLanguage: String,
                              testingFramework: String,
                              numberOfTasks: Int
    ): Course {
        try {
            val request = post("/course/create",
                    parameters = mapOf("courseName" to courseName,
                            "description" to description,
                            "language" to language,
                            "testingLanguage" to testingLanguage,
                            "testingFramework" to testingFramework,
                            "numberOfTasks" to numberOfTasks),
                    credentials = credentials)
            if (request.status.toInt() == 200) {
                return courseFromDynamic(JSON.parse<Payload<dynamic>>(request.responseText).payload)
            } else {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Course creation call failed.", e)
        }
    }

    override fun getAvailableLanguages(): List<Language> {
        try {
            val request = get("/settings/languages")
            if (request.status.toInt() == 200) {
                return JSON.parse<Payload<Array<dynamic>>>(request.responseText)
                        .payload
                        ?.toList()
                        ?.map { languageFromDynamic(it) }
                        ?: emptyList()
            } else {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Available languages retrieving failed.", e)
        }
    }

    override fun getCourseStatistics(credentials: Credentials,
                                     username: String,
                                     courseName: String
    ): CourseStatistics {
        try {
            val request = get("/statistics",
                    parameters = mapOf("owner" to username, "course" to courseName),
                    credentials = credentials)
            if (request.status.toInt() == 200) {
                return courseStatisticsFromDynamic(JSON.parse<Payload<dynamic>>(request.responseText).payload)
            } else {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Course statistics retrieving failed.", e)
        }
    }

    override fun startCourse(credentials: Credentials, courseName: String): Course {
        try {
            val request = post("/course/activate",
                    parameters = mapOf("courseName" to courseName),
                    credentials = credentials)
            if (request.status.toInt() == 200) {
                return courseFromDynamic(JSON.parse<Payload<dynamic>>(request.responseText).payload)
            } else {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Course starting failed.", e)
        }
    }

    override fun deleteCourse(credentials: Credentials, courseName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun analysePlagiarism(credentials: Credentials, courseName: String) {
        try {
            val request = post("/course/analyse/plagiarism",
                    parameters = mapOf("courseName" to courseName),
                    credentials = credentials)
            if (request.status.toInt() != 200) {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Plagiarism analysis scheduling failed.", e)
        }
    }

    override fun syncCourse(credentials: Credentials, courseName: String) {
        try {
            val request = post("/course/sync",
                    parameters = mapOf("courseName" to courseName),
                    credentials = credentials)
            if (request.status.toInt() != 200) {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Course synchronization failed.", e)
        }
    }

    override fun updateRules(credentials: Credentials,
                             courseName: String,
                             task: String,
                             deadline: String?
    ) {
        try {
            val request = post("/task/update/rules",
                    parameters = mapOf("courseName" to courseName,
                            "taskBranch" to task,
                            "deadline" to deadline),
                    credentials = credentials)
            if (request.status.toInt() != 200) {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Task rules updating failed.", e)
        }
    }

    override fun updateScores(credentials: Credentials,
                              courseName: String,
                              task: String,
                              scores: Map<String, Int>
    ) {
        try {
            val request = post("/task/update/scores",
                    parameters = mapOf("courseName" to courseName,
                            "taskBranch" to task),
                    body = scores.map { (a, b) -> "\"$a\": $b" }
                            .joinToString(", ", "{", "}")
                            .let { JSON.parse<Map<String, Int>>(it) },
                    credentials = credentials)
            if (request.status.toInt() != 200) {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Task scores updating failed.", e)
        }
    }

    override fun addCodacyToken(credentials: Credentials, codacyToken: String) {
        try {
            val request = put("/codacy/token",
                    parameters = mapOf("token" to codacyToken),
                    credentials = credentials)
            if (request.status.toInt() != 200) {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Codacy token addition failed.", e)
        }
    }

    override fun activateCodacy(credentials: Credentials, courseName: String) {
        try {
            val request = post("/course/activate/codacy",
                    parameters = mapOf("courseName" to courseName),
                    credentials = credentials)
            if (request.status.toInt() != 200) {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Activating codacy for course failed.", e)
        }
    }

    override fun activateTravis(credentials: Credentials, courseName: String) {
        try {
            val request = post("/course/activate/travis",
                    parameters = mapOf("courseName" to courseName),
                    credentials = credentials)
            if (request.status.toInt() != 200) {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Activating travis for course failed.", e)
        }
    }

    override fun downloadStatistics(credentials: Credentials, courseName: String, format: String): dynamic {
        try {
            val request = get("/statistics/download",
                    parameters = mapOf("courseName" to courseName, "format" to format),
                    credentials = credentials)
            if (request.status.toInt() == 200) {
                return request.response
            } else {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Course statistics retrieving failed.", e)
        }
    }

    override fun getGithubAuthData(credentials: Credentials): GithubAuthData {
        try {
            val request = get("/github/auth", credentials = credentials)
            if (request.status.toInt() == 200) {
                return githubAuthDataFromDynamic(JSON.parse<Payload<dynamic>>(request.responseText).payload)
            } else {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Course statistics retrieving failed.", e)
        }
    }

    private fun get(method: String,
                    parameters: Map<String, Any?> = emptyMap(),
                    body: Any? = null,
                    credentials: Credentials? = null
    ): XMLHttpRequest = httpCall("GET", method, parameters, body, credentials)

    private fun post(method: String,
                     parameters: Map<String, Any?> = emptyMap(),
                     body: Any? = null,
                     credentials: Credentials? = null
    ): XMLHttpRequest = httpCall("POST", method, parameters, body, credentials)

    private fun put(method: String,
                    parameters: Map<String, Any?> = emptyMap(),
                    body: Any? = null,
                    credentials: Credentials? = null
    ): XMLHttpRequest = httpCall("PUT", method, parameters, body, credentials)

    private fun httpCall(httpMethod: String,
                         apiMethod: String,
                         parameters: Map<String, Any?>,
                         body: Any?,
                         credentials: Credentials?
    ): XMLHttpRequest = escapedHttpCall(httpMethod, apiMethod, parameters, body, credentials)

    private fun escapedHttpCall(httpMethod: String,
                                apiMethod: String,
                                parameters: Map<String, Any?>,
                                body: Any?,
                                credentials: Credentials?
    ) = XMLHttpRequest().apply {
        if (parameters.isEmpty()) {
            open(httpMethod, "$baseUrl$apiMethod", async = false)
        } else {
            val parametersString = parameters.filterValues { it != null }
                    .map { (key, value) -> "$key=${encodeURIComponent(value.toString())}" }
                    .joinToString("&")
            open(httpMethod, "$baseUrl$apiMethod?$parametersString", async = false)
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

    private fun authorizationToken(credentials: Credentials) =
            "Basic " + btoa(credentials.username + ":" + credentials.password)

}