package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.entity.CourseEntity
import com.tcibinan.flaxo.model.entity.toDtos

/**
 * Course data object.
 */
data class Course(private val entity: CourseEntity)
    : DataObject<CourseEntity> {

    val id: Long
            by lazy { entity.courseId ?: missing("id") }
    val name: String
            by lazy { entity.name ?: missing("name") }
    val language: String
            by lazy { entity.language ?: missing("language") }
    val testingLanguage: String
            by lazy { entity.testingLanguage ?: missing("testingLanguage") }
    val testingFramework: String
            by lazy { entity.testingFramework ?: missing("testingFramework") }
    val status: String
            by lazy { entity.status ?: missing("status") }
    val user: User
            by lazy { User(entity.user ?: missing("user")) }
    val students: Set<Student>
            by lazy { entity.students.toDtos() }
    val tasks: Set<Task>
            by lazy { entity.tasks.toDtos() }

    override fun toEntity() = entity

    override fun view(): Any = let { course ->
        object {
            val id = course.id
            val name = course.name
            val language = course.language
            val testingLanguage = course.testingLanguage
            val testingFramework = course.testingFramework
            val status = course.status
            val user = course.user.nickname
            val userGithubId = course.user.githubId
            val students = course.students.map { it.nickname }
            val tasks = course.tasks.map { it.name }
        }
    }

    fun with(id: Long? = null,
             name: String? = null,
             language: String? = null,
             testingLanguage: String? = null,
             testingFramework: String? = null,
             status: String? = null,
             user: User? = null,
             students: Set<Student> = emptySet(),
             tasks: Set<Task> = emptySet()
    ) = CourseEntity()
            .also {
                it.courseId = id ?: entity.courseId
                it.name = name ?: entity.name
                it.language = language ?: entity.language
                it.testingLanguage = testingLanguage ?: entity.testingLanguage
                it.testingFramework = testingFramework ?: entity.testingFramework
                it.status = status ?: entity.status
                it.user = user?.toEntity() ?: entity.user
                it.students = students.takeIf { it.isNotEmpty() }?.toEntities() ?: entity.students
                it.tasks = tasks.takeIf { it.isNotEmpty() }?.toEntities() ?: entity.tasks
            }
            .toDto()
}