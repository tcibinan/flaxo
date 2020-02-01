package org.flaxo.frontend.client

import kotlinx.serialization.list
import kotlinx.serialization.map
import kotlinx.serialization.serializer
import org.flaxo.common.data.Course
import org.flaxo.common.data.CourseSettings
import org.flaxo.common.data.CourseStatistics
import org.flaxo.common.data.GithubAuthData
import org.flaxo.common.data.PlagiarismReport
import org.flaxo.common.data.Solution
import org.flaxo.common.data.SolutionReview
import org.flaxo.common.data.Task
import org.flaxo.common.data.User
import org.flaxo.frontend.Container
import org.flaxo.frontend.Credentials

/**
 * Flaxo client implementation based on the [HttpRequest] client.
 */
class XMLHttpRequestFlaxoClient(private val baseUrl: String) : FlaxoClient {

    private companion object {
        const val COURSE_NAME = "courseName"
        const val TASK_BRANCH = "taskBranch"
        const val NICKNAME = "nickname"
        const val PASSWORD = "password"
        const val DESCRIPTION = "description"
        const val PRIVATE = "private"
        const val LANGUAGE = "language"
        const val TESTING_LANGUAGE = "testingLanguage"
        const val TESTING_FRAMEWORK = "testingFramework"
        const val NUMBER_OF_TASKS = "numberOfTasks"
        const val COURSE = "course"
        const val OWNER = "owner"
        const val DEADLINE = "deadline"
        const val TOKEN = "token"
        const val FORMAT = "format"
        val json = Container.json
    }

    override suspend fun registerUser(credentials: Credentials): User =
            post {
                apiMethod = "/user/register"
                params = mapOf(NICKNAME to credentials.username, PASSWORD to credentials.password)
                onSuccess = { response -> json.parse(User.serializer(), response) }
                errorMessage = "User registering failed."
            }

    override suspend fun getSelf(credentials: Credentials): User =
            get {
                apiMethod = "/user"
                creds = credentials
                onSuccess = { response -> json.parse(User.serializer(), response) }
                errorMessage = "Current user retrieving failed."
            }

    override suspend fun getUserCourses(credentials: Credentials, username: String): List<Course> =
            get {
                apiMethod = "/course/all"
                params = mapOf(NICKNAME to username)
                creds = credentials
                onSuccess = { response -> json.parse(Course.serializer().list, response) }
                errorMessage = "User courses retrieving failed."
            }

    override suspend fun createCourse(credentials: Credentials,
                                      courseName: String,
                                      description: String?,
                                      private: Boolean,
                                      language: String?,
                                      testingLanguage: String?,
                                      testingFramework: String?,
                                      numberOfTasks: Int
    ): Course =
            post {
                apiMethod = "/course/create"
                params = mapOf(COURSE_NAME to courseName,
                        DESCRIPTION to description,
                        PRIVATE to private,
                        LANGUAGE to language,
                        TESTING_LANGUAGE to testingLanguage,
                        TESTING_FRAMEWORK to testingFramework,
                        NUMBER_OF_TASKS to numberOfTasks)
                creds = credentials
                onSuccess = { response -> json.parse(Course.serializer(), response) }
                errorMessage = "Course creation failed."
            }

    override suspend fun importCourse(credentials: Credentials, courseName: String, description: String?): Course =
            post {
                apiMethod = "/course/import"
                params = mapOf(COURSE_NAME to courseName,
                        DESCRIPTION to description)
                creds = credentials
                onSuccess = { response -> json.parse(Course.serializer(), response) }
                errorMessage = "Course importing failed."
            }

    override suspend fun getCourseStatistics(credentials: Credentials,
                                             username: String,
                                             courseName: String
    ): CourseStatistics =
            get {
                apiMethod = "/statistics"
                params = mapOf(OWNER to username, COURSE to courseName)
                creds = credentials
                onSuccess = { response -> json.parse(CourseStatistics.serializer(), response) }
                errorMessage = "Course statistics retrieving failed."
            }

    override suspend fun startCourse(credentials: Credentials, courseName: String): Course =
            post {
                apiMethod = "/course/activate"
                params = mapOf(COURSE_NAME to courseName)
                creds = credentials
                onSuccess = { response -> json.parse(Course.serializer(), response) }
                errorMessage = "Course starting failed."
            }

    override suspend fun updateCourseSetting(credentials: Credentials, id: Long, settings: CourseSettings): Course =
            put {
                apiMethod = "/course/$id/settings"
                body = json.stringify(CourseSettings.serializer(), settings)
                creds = credentials
                onSuccess = { response -> json.parse(Course.serializer(), response) }
                errorMessage = "Course settings update has failed."
            }

    override suspend fun deleteCourse(credentials: Credentials, courseName: String) {
        // TODO 23.03.19: Delete course but not the repository.
    }

    override suspend fun analysePlagiarism(credentials: Credentials,
                                           courseName: String,
                                           task: String
    ): PlagiarismReport =
            post {
                apiMethod = "/plagiarism/analyse"
                params = mapOf(COURSE_NAME to courseName, TASK_BRANCH to task)
                creds = credentials
                onSuccess = { response -> json.parse(PlagiarismReport.serializer(), response) }
                errorMessage = "Plagiarism analysis failed."
            }

    override suspend fun syncCourse(credentials: Credentials, courseName: String): CourseStatistics =
            post {
                apiMethod = "/course/sync"
                params = mapOf(COURSE_NAME to courseName)
                creds = credentials
                onSuccess = { response -> json.parse(CourseStatistics.serializer(), response) }
                errorMessage = "Course synchronization failed."
            }

    override suspend fun updateRules(credentials: Credentials,
                                     courseName: String,
                                     task: String,
                                     deadline: String?
    ): Task =
            post {
                apiMethod = "/task/update/rules"
                params = mapOf(COURSE_NAME to courseName, TASK_BRANCH to task, DEADLINE to deadline)
                creds = credentials
                onSuccess = { response -> json.parse(Task.serializer(), response) }
                errorMessage = "Task rules updating failed."
            }

    override suspend fun updateScores(credentials: Credentials,
                                      courseName: String,
                                      task: String,
                                      scores: Map<String, Int>
    ): List<Solution> =
            post {
                apiMethod = "/task/update/scores"
                params = mapOf(COURSE_NAME to courseName, TASK_BRANCH to task)
                creds = credentials
                body = json.stringify((String.serializer() to Int.serializer()).map, scores)
                onSuccess = { response -> json.parse(Solution.serializer().list, response) }
                errorMessage = "Task scores updating failed."
            }

    override suspend fun addCodacyToken(credentials: Credentials, codacyToken: String): Unit =
            put {
                apiMethod = "/codacy/token"
                params = mapOf(TOKEN to codacyToken)
                creds = credentials
                errorMessage = "Codacy token addition failed."
            }

    override suspend fun activateCodacy(credentials: Credentials, courseName: String): Unit =
            post {
                apiMethod = "/course/activate/codacy"
                params = mapOf(COURSE_NAME to courseName)
                creds = credentials
                errorMessage = "Activating codacy for course failed."
            }

    override suspend fun activateTravis(credentials: Credentials, courseName: String): Unit =
            post {
                apiMethod = "/course/activate/travis"
                creds = credentials
                params = mapOf(COURSE_NAME to courseName)
                errorMessage = "Activating travis for course failed."
            }

    override suspend fun activateGitplag(credentials: Credentials, courseName: String): Unit =
            post {
                apiMethod = "/course/activate/gitplag"
                creds = credentials
                params = mapOf(COURSE_NAME to courseName)
                errorMessage = "Activating gitplag for course failed."
            }

    override suspend fun downloadStatistics(credentials: Credentials, courseName: String, format: String): dynamic =
            get<Any> {
                apiMethod = "/statistics/download"
                creds = credentials
                params = mapOf(COURSE_NAME to courseName, FORMAT to format)
                onSuccess = { response -> response }
                errorMessage = "Course statistics downloading failed."
            }.asDynamic()

    override suspend fun getGithubAuthData(credentials: Credentials): GithubAuthData =
            get {
                apiMethod = "/github/auth"
                creds = credentials
                onSuccess = { response -> json.parse(GithubAuthData.serializer(), response) }
                errorMessage = "Github auth data retrieving failed."
            }

    override suspend fun updateSolutionApprovals(credentials: Credentials,
                                                 courseName: String,
                                                 task: String,
                                                 approvals: Map<String, SolutionReview>
    ): List<Solution> =
            post {
                apiMethod = "/task/update/approvals"
                params = mapOf(COURSE_NAME to courseName, TASK_BRANCH to task)
                creds = credentials
                body = json.stringify((String.serializer() to SolutionReview.serializer()).map, approvals)
                onSuccess = { response -> json.parse(Solution.serializer().list, response) }
                errorMessage = "Task approvals updating failed."
            }

    override suspend fun getPlagiarismGraphAccessToken(credentials: Credentials,
                                                       courseName: String,
                                                       task: String
    ): String = post {
        apiMethod = "/plagiarism/graph/token"
        params = mapOf(COURSE_NAME to courseName, TASK_BRANCH to task)
        creds = credentials
        onSuccess = { response -> json.parse(String.serializer(), response) }
        errorMessage = "Plagiarism graph access token generation retrieving failed."
    }

    private suspend inline fun <reified T> get(noinline block: HttpRequest<T>.() -> Unit): T = http("GET", block)

    private suspend inline fun <reified T> post(noinline block: HttpRequest<T>.() -> Unit): T = http("POST", block)

    private suspend inline fun <reified T> put(noinline block: HttpRequest<T>.() -> Unit): T = http("PUT", block)

    private suspend inline fun <reified T> http(method: String, noinline block: HttpRequest<T>.() -> Unit): T {
        val wrappedBlock: HttpRequest<T>.() -> Unit
        if (T::class == Unit::class) {
            wrappedBlock = {
                onSuccess = { Unit as T }
                block()
            }
        } else {
            wrappedBlock = block
        }

        try {
            return HttpRequest<T>().run {
                url = baseUrl
                httpMethod = method
                wrappedBlock()
                execute()
            }
        } catch (e: FlaxoHttpException) {
            throw FlaxoHttpException(message = e.message, cause = e, userMessage = e.userMessage)
        } catch (e: Throwable) {
            throw FlaxoHttpException(cause = e)
        }
    }
}
