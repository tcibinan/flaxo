package com.tcibinan.flaxo.core

import com.tcibinan.flaxo.core.model.Course
import com.tcibinan.flaxo.core.model.Student
import com.tcibinan.flaxo.core.model.Task
import com.tcibinan.flaxo.core.model.User

interface DataService {
    fun addUser(nickname: String,
                password: String): User

    fun getUser(nickname: String): User?

    fun createCourse(name: String,
                     language: String,
                     testLanguage: String,
                     testingFramework: String,
                     numberOfTasks: Int,
                     owner: User): Course

    fun getCourse(name: String,
                  owner: User): Course?

    fun addStudent(nickname: String,
                   course: Course): Student

    fun getStudents(course: Course): Set<Student>

    fun getTasks(course: Course): Set<Task>
}