package org.flaxo.frontend.client

import org.flaxo.frontend.data.*
import org.flaxo.frontend.data.Language
import org.flaxo.frontend.wrapper.btoa
import org.w3c.xhr.XMLHttpRequest

class PlainHttpFlaxoClient(private val baseUrl: String) : FlaxoClient {

    override fun registerUser(credentials: Credentials): User {
        try {
            val request = XMLHttpRequest()
            request.open("POST",
                    "$baseUrl/user/register?nickname=${credentials.username}&password=${credentials.password}",
                    async = false)
            request.send(JSON.stringify(credentials))
            if (request.status.toInt() == 200) {
                return JSON.parse<Payload<User>>(request.responseText)
                        .payload
                        ?: throw FlaxoHttpCallException("There is no flaxo user in server response")
            } else {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("User registering has failed.", e)
        }
    }

    override fun getSelf(credentials: Credentials): User {
        try {
            val request = XMLHttpRequest()
            request.open("GET", "$baseUrl/user", async = false)
            request.setRequestHeader("Authorization", authorizationToken(credentials))
            request.send()
            if (request.status.toInt() == 200) {
                return JSON.parse<Payload<User>>(request.responseText)
                        .payload
                        ?: throw FlaxoHttpCallException("There is no flaxo user in server response")
            } else {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Current user retrieving failed.", e)
        }
    }

    private fun authorizationToken(credentials: Credentials) =
            "Basic " + btoa(credentials.username + ":" + credentials.password)

    override fun getUserCourses(credentials: Credentials, username: String): List<Course> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createCourse(credentials: Credentials, courseParameters: CourseParameters): Course {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAvailableLanguages(): List<Language> {
        try {
            val request = XMLHttpRequest()
            request.open("GET", "$baseUrl/settings/languages", async = false)
            request.send()
            if (request.status.toInt() == 200) {
                return JSON.parse<Payload<Array<Language>>>(request.responseText)
                        .payload
                        ?.toList()
                        ?: emptyList()
            } else {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Available languages retrieving failed.", e)
        }
    }

    override fun getCourseStatistics(credentials: Credentials, username: String, courseName: String): CourseStatistics {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startCourse(credentials: Credentials, courseName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteCourse(credentials: Credentials, courseName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun analysePlagiarism(credentials: Credentials, courseName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun syncCourse(credentials: Credentials, courseName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateRules(credentials: Credentials, courseName: String, task: String, deadline: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addCodacyToken(credentials: Credentials, codacyToken: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun activateCodacy(credentials: Credentials, courseName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun activateTravis(credentials: Credentials, courseName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun downloadStatistics(credentials: Credentials, courseName: String, format: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}