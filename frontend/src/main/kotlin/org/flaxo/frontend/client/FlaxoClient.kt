package org.flaxo.frontend.client

import org.flaxo.common.Course
import org.flaxo.common.CourseStatistics
import org.flaxo.common.GithubAuthData
import org.flaxo.common.Language
import org.flaxo.common.Solution
import org.flaxo.common.SolutionReview
import org.flaxo.common.User
import org.flaxo.frontend.Credentials

interface FlaxoClient {

    suspend fun registerUser(credentials: Credentials): User

    suspend fun getSelf(credentials: Credentials): User

    suspend fun getUserCourses(credentials: Credentials, username: String): List<Course>

    suspend fun createCourse(credentials: Credentials,
                             courseName: String,
                             description: String? = null,
                             language: String,
                             testingLanguage: String,
                             testingFramework: String,
                             numberOfTasks: Int
    ): Course

    suspend fun getAvailableLanguages(): List<Language>

    suspend fun getCourseStatistics(credentials: Credentials, username: String, courseName: String): CourseStatistics

    suspend fun startCourse(credentials: Credentials, courseName: String): Course

    suspend fun deleteCourse(credentials: Credentials, courseName: String)

    suspend fun analysePlagiarism(credentials: Credentials, courseName: String, task: String)

    suspend fun syncCourse(credentials: Credentials, courseName: String)

    suspend fun updateRules(credentials: Credentials, courseName: String, task: String, deadline: String?)

    suspend fun updateScores(credentials: Credentials, courseName: String, task: String, scores: Map<String, Int>)

    suspend fun addCodacyToken(credentials: Credentials, codacyToken: String)

    suspend fun activateCodacy(credentials: Credentials, courseName: String)

    suspend fun activateTravis(credentials: Credentials, courseName: String)

    suspend fun downloadStatistics(credentials: Credentials, courseName: String, format: String): dynamic

    suspend fun getGithubAuthData(credentials: Credentials): GithubAuthData

    suspend fun updateSolutionApprovals(credentials: Credentials,
                                        courseName: String,
                                        task: String,
                                        approvals: Map<String, SolutionReview>
    ): List<Solution>
}
