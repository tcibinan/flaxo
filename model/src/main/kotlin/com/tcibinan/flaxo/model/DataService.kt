package com.tcibinan.flaxo.model

import com.tcibinan.flaxo.model.data.Course
import com.tcibinan.flaxo.model.data.Credentials
import com.tcibinan.flaxo.model.data.Student
import com.tcibinan.flaxo.model.data.Task
import com.tcibinan.flaxo.model.data.User

interface DataService {
    fun addUser(nickname: String,
                password: String): User

    fun getUser(nickname: String): User?

    fun createCourse(name: String,
                     language: String,
                     testLanguage: String,
                     testingFramework: String,
                     gitRepositoryId: String,
                     numberOfTasks: Int,
                     owner: User): Course

    fun deleteCourse(courseName: String,
                     owner: User)

    fun updateCourse(updatedCourse: Course)

    fun getCourse(name: String,
                  owner: User): Course?

    fun getCourses(userNickname: String): Set<Course>

    fun addStudent(nickname: String,
                   course: Course): Student

    fun getStudents(course: Course): Set<Student>

    fun getTasks(course: Course): Set<Task>

    fun addToken(userNickname: String,
                 service: IntegratedService,
                 accessToken: String): User

    fun addGithubId(userNickname: String, githubId: String): User
}
