package org.flaxo.frontend.client

import org.flaxo.frontend.data.*
import org.flaxo.frontend.wrapper.btoa
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Date

class PlainHttpFlaxoClient(private val baseUrl: String) : FlaxoClient {

    override fun registerUser(credentials: Credentials): User {
        try {
            val request = XMLHttpRequest()
            request.open("POST",
                    "$baseUrl/user/register?nickname=${credentials.username}&password=${credentials.password}",
                    async = false)
            request.send(JSON.stringify(credentials))
            if (request.status.toInt() == 200) {
                return userFromDynamic(JSON.parse<Payload<dynamic>>(request.responseText).payload)
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
                return userFromDynamic(JSON.parse<Payload<dynamic>>(request.responseText).payload)
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
        try {
            val request = XMLHttpRequest()
            request.open("GET", "$baseUrl/course/all?nickname=$username", async = false)
            request.setRequestHeader("Authorization", authorizationToken(credentials))
            request.send()
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

    private fun courseFromDynamic(courseJson: dynamic): Course =
            Course(name = courseJson.name,
                    state = courseStateFromDynamic(courseJson.state),
                    language = courseJson.language,
                    testingLanguage = courseJson.testingLanguage,
                    testingFramework = courseJson.testingFramework,
                    description = courseJson.description,
                    createdDate = Date(courseJson.createdDate as String),
                    tasks = (courseJson.tasks as Array<String>).toList(),
                    students = (courseJson.students as Array<String>).toList(),
                    url = courseJson.url,
                    user = userFromDynamic(courseJson.user))

    private fun userFromDynamic(userJson: dynamic): User {
        return User(
                githubId = userJson.githubId,
                nickname = userJson.nickname,
                isGithubAuthorized = userJson.githubAuthorized,
                isTravisAuthorized = userJson.travisAuthorized,
                isCodacyAuthorized = userJson.codacyAuthorized
        )
    }

    private fun courseStateFromDynamic(courseStateJson: dynamic): CourseState {
        return CourseState(
                lifecycle = CourseLifecycle.valueOf(courseStateJson.lifecycle),
                activatedServices = (courseStateJson.activatedServices as Array<String>).toList()
        )
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

    // todo: Replace with kotlinx.serialization features
    private fun languageFromDynamic(languageJson: dynamic) =
            Language(name = languageJson.name,
                    compatibleTestingLanguages = (languageJson.compatibleTestingLanguages as Array<String>).toList(),
                    compatibleTestingFrameworks = (languageJson.compatibleTestingFrameworks as Array<String>).toList())

    override fun getCourseStatistics(credentials: Credentials,
                                     username: String,
                                     courseName: String): CourseStatistics {
        try {
            val request = XMLHttpRequest()
            request.open("GET", "$baseUrl/statistics?owner=$username&course=$courseName", async = false)
            request.send()
            if (request.status.toInt() == 200) {
                return courseStatisticsFromDynamic(JSON.parse<Payload<dynamic>>(request.responseText).payload)
            } else {
                throw FlaxoHttpCallException(request.responseText)
            }
        } catch (e: Throwable) {
            throw FlaxoHttpCallException("Course statistics retrieving failed.", e)
        }
    }

    private fun courseStatisticsFromDynamic(courseStatisticsJson: dynamic): CourseStatistics =
            CourseStatistics(
                    tasks = (courseStatisticsJson.tasks as Array<dynamic>).toList()
                            .map { taskFromDynamic(it) }
            )

    private fun taskFromDynamic(taskJson: dynamic): Task =
            Task(
                    branch = taskJson.branch,
                    deadline = nullableDateFromDynamic(taskJson.deadline),
                    plagiarismReports = (taskJson.plagiarismReports as Array<dynamic>).toList()
                            .map { plagiarismReportFromDynamic(it) },
                    url = taskJson.url,
                    solutions = (taskJson.solutions as Array<dynamic>).toList()
                            .map { solutionFromDynamic(it) }
            )

    private fun solutionFromDynamic(solutionJson: dynamic): Solution =
            Solution(
                    task = solutionJson.task,
                    student = solutionJson.student,
                    score = solutionJson.score,
                    commits = (solutionJson.commits as Array<dynamic>).toList()
                            .map { commitFromDynamic(it) },
                    buildReports = (solutionJson.buildReports as Array<dynamic>).toList()
                            .map { buildReportFromDynamic(it) },
                    codeStyleReports = (solutionJson.codeStyleReports as Array<dynamic>).toList()
                            .map { codeStyleReportFromDynamic(it) }
            )

    private fun codeStyleReportFromDynamic(codeStyleReportJson: dynamic): CodeStyleReport =
            CodeStyleReport(
                    grade = codeStyleReportJson.grade,
                    date = Date(codeStyleReportJson.date as String)
            )

    private fun buildReportFromDynamic(buildReportJson: dynamic): BuildReport =
            BuildReport(
                    succeed = buildReportJson.succeed,
                    date = Date(buildReportJson.date as String)
            )

    private fun commitFromDynamic(commitJson: dynamic): Commit =
            Commit(
                    sha = commitJson.sha,
                    date = nullableDateFromDynamic(commitJson.date)
            )

    private fun plagiarismReportFromDynamic(plagiarismReportJson: dynamic): PlagiarismReport =
            PlagiarismReport(
                    url = plagiarismReportJson.url,
                    date = Date(plagiarismReportJson.date as String),
                    matches = (plagiarismReportJson.matches as Array<dynamic>).toList()
                            .map { plagiarismMatchFromDynamic(it) }
            )

    private fun plagiarismMatchFromDynamic(plagiarismMatchJson: dynamic): PlagiarismMatch =
            PlagiarismMatch(
                    url = plagiarismMatchJson.url,
                    student1 = plagiarismMatchJson.student1,
                    student2 = plagiarismMatchJson.student2,
                    lines = plagiarismMatchJson.lines,
                    percentage = plagiarismMatchJson.percentage
            )

    private fun nullableDateFromDynamic(dateString: String?): Date? = dateString?.let { Date(it) }

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

    override fun updateRules(credentials: Credentials, courseName: String, task: String, deadline: String?) {
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

    override fun getGithubAuthData(): GithubAuthData {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}