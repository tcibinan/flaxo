package org.flaxo.frontend.client

import org.flaxo.common.data.Course
import org.flaxo.common.data.CourseSettings
import org.flaxo.common.data.CourseStatistics
import org.flaxo.common.data.GithubAuthData
import org.flaxo.common.data.PlagiarismReport
import org.flaxo.common.data.Solution
import org.flaxo.common.data.SolutionReview
import org.flaxo.common.data.Task
import org.flaxo.common.data.User
import org.flaxo.frontend.Credentials

/**
 * Flaxo backend client.
 */
interface FlaxoClient {

    /**
     * Register user with the following [credentials] in flaxo system.
     */
    suspend fun registerUser(credentials: Credentials): User

    /**
     * Retrieves registered user by the given [credentials].
     */
    suspend fun getSelf(credentials: Credentials): User

    /**
     * Retrieves all courses of user with the given [username].
     */
    suspend fun getUserCourses(credentials: Credentials, username: String): List<Course>

    /**
     * Creates a new course with all the given parameters.
     */
    suspend fun createCourse(credentials: Credentials,
                             courseName: String,
                             description: String? = null,
                             private: Boolean = false,
                             language: String? = null,
                             testingLanguage: String? = null,
                             testingFramework: String? = null,
                             numberOfTasks: Int
    ): Course

    /**
     * Imports an existing course by the given [courseName] with the given description.
     */
    suspend fun importCourse(credentials: Credentials, courseName: String, description: String?): Course

    /**
     * Retrieves statistics of the course with the given [courseName] and owner [username].
     */
    suspend fun getCourseStatistics(credentials: Credentials, username: String, courseName: String): CourseStatistics

    /**
     * Starts a course with the given [courseName].
     */
    suspend fun startCourse(credentials: Credentials, courseName: String): Course

    /**
     * Updates course with [id] settings.
     */
    suspend fun updateCourseSetting(credentials: Credentials, id: Long, settings: CourseSettings): Course

    /**
     * Deletes a course with the given [courseName] from flaxo system.
     */
    suspend fun deleteCourse(credentials: Credentials, courseName: String)

    /**
     * Performs a plagiarism analysis of a particular [task] in the course with [courseName].
     */
    suspend fun analysePlagiarism(credentials: Credentials, courseName: String, task: String): PlagiarismReport

    /**
     * Synchronizes course statistics with all the external vendors: github, travis, codacy.
     */
    suspend fun syncCourse(credentials: Credentials, courseName: String): CourseStatistics

    /**
     * Updates a particular [task] rules ([deadline]) of the course with [courseName].
     */
    suspend fun updateRules(credentials: Credentials, courseName: String, task: String, deadline: String?): Task

    /**
     * Updates [scores] of the [task] of the course with [courseName].
     */
    suspend fun updateScores(credentials: Credentials,
                             courseName: String,
                             task: String,
                             scores: Map<String, Int>
    ): List<Solution>

    /**
     * Adds a [codacyToken] to a user account.
     */
    suspend fun addCodacyToken(credentials: Credentials, codacyToken: String)

    /**
     * Launches codacy activation for the given [courseName].
     */
    suspend fun activateCodacy(credentials: Credentials, courseName: String)

    /**
     * Launches travis activation for the given [courseName].
     */
    suspend fun activateTravis(credentials: Credentials, courseName: String)

    /**
     * Launches gitplag activation for the given [courseName].
     */
    suspend fun activateGitplag(credentials: Credentials, courseName: String)

    /**
     * Downloads statistics file for the course with [courseName] in the given [format].
     */
    suspend fun downloadStatistics(credentials: Credentials, courseName: String, format: String): dynamic

    /**
     * Retrieves github auth data for the current user.
     */
    suspend fun getGithubAuthData(credentials: Credentials): GithubAuthData

    /**
     * Updates a particular [task] solutions [approvals] for the course with the given [courseName].
     */
    suspend fun updateSolutionApprovals(credentials: Credentials,
                                        courseName: String,
                                        task: String,
                                        approvals: Map<String, SolutionReview>
    ): List<Solution>

    /**
     * Retrieves [task] latest plagiarism graph access token for the course with the given [courseName].
     */
    suspend fun getPlagiarismGraphAccessToken(credentials: Credentials,
                                              courseName: String,
                                              task: String
    ): String
}
