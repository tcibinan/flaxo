package org.flaxo.frontend.client

import org.flaxo.frontend.data.*

interface FlaxoClient {
    fun registerUser(credentials: Credentials): User
    fun getSelf(credentials: Credentials): User
    fun getUserCourses(credentials: Credentials, username: String): List<Course>
    fun createCourse(credentials: Credentials, courseParameters: CourseParameters): Course
    fun getAvailableLanguages(): List<Language>
    fun getCourseStatistics(credentials: Credentials, username: String, courseName: String): CourseStatistics
    fun startCourse(credentials: Credentials, courseName: String)
    fun deleteCourse(credentials: Credentials, courseName: String)
    fun analysePlagiarism(credentials: Credentials, courseName: String)
    fun syncCourse(credentials: Credentials, courseName: String)
    fun updateRules(credentials: Credentials, courseName: String, task: String, deadline: String)
    fun addCodacyToken(credentials: Credentials, codacyToken: String)
    fun activateCodacy(credentials: Credentials, courseName: String)
    fun activateTravis(credentials: Credentials, courseName: String)
    fun downloadStatistics(credentials: Credentials, courseName: String, format: String)
}