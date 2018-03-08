package com.tcibinan.flaxo.model.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.tcibinan.flaxo.model.EntityFieldIsAbsent
import com.tcibinan.flaxo.model.entity.CourseEntity
import com.tcibinan.flaxo.model.entity.toDtos

/**
 * Course data object.
 */
@JsonIgnoreProperties("students", "tasks")
data class Course(private val entity: CourseEntity)
    : DataObject<CourseEntity> {

    val id: Long
            by lazy { entity.courseId ?: throw EntityFieldIsAbsent("course", "id") }
    val name: String
            by lazy { entity.name ?: throw EntityFieldIsAbsent("course", "name") }
    val language: String
            by lazy { entity.language ?: throw EntityFieldIsAbsent("course", "language") }
    val testingLanguage: String
            by lazy { entity.testingLanguage ?: throw EntityFieldIsAbsent("course", "testingLanguage") }
    val testingFramework: String
            by lazy { entity.testingFramework ?: throw EntityFieldIsAbsent("course", "testingFramework") }
    val status: String
            by lazy { entity.status ?: throw EntityFieldIsAbsent("course", "status") }
    val user: User
            by lazy { User(entity.user ?: throw EntityFieldIsAbsent("course", "user")) }
    val students: Set<Student>
            by lazy { entity.students.toDtos() }
    val tasks: Set<Task>
            by lazy { entity.tasks.toDtos() }

    override fun toEntity() = entity

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