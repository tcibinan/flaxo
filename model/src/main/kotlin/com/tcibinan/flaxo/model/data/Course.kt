package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.EntityFieldIsAbsent
import com.tcibinan.flaxo.model.entity.CourseEntity
import com.tcibinan.flaxo.model.entity.toDtos

data class Course(private val entity: CourseEntity) : DataObject<CourseEntity> {

    val id: Long by lazy { entity.course_id ?: throw EntityFieldIsAbsent("course", "id") }
    val name: String by lazy { entity.name ?: throw EntityFieldIsAbsent("course", "name") }
    val language: String by lazy { entity.language ?: throw EntityFieldIsAbsent("course", "language") }
    val testingLanguage: String by lazy {
        entity.test_language ?: throw EntityFieldIsAbsent("course", "testingLanguage")
    }
    val testingFramework: String by lazy {
        entity.testing_framework ?: throw EntityFieldIsAbsent("course", "testingFramework")
    }
    val githubRepositoryId: String by lazy { entity.github_repository_id ?: throw EntityFieldIsAbsent("course", "github repository id") }
    val status: String by lazy { entity.status ?: throw EntityFieldIsAbsent("course", "status") }
    val user: User by lazy { User(entity.user ?: throw EntityFieldIsAbsent("course", "user")) }
    val students: Set<Student> by lazy { entity.students.toDtos() }
    val tasks: Set<Task> by lazy { entity.tasks.toDtos() }

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
            .apply {
                this.course_id = id ?: entity.course_id
                this.name = name ?: entity.name
                this.language = language ?: entity.language
                this.language = testingLanguage ?: entity.language
                this.test_language = testingFramework ?: entity.test_language
                this.status = status ?: entity.status
                this.user = user?.toEntity() ?: entity.user
                this.students = if (students.isNotEmpty()) students.toEntities() else entity.students
                this.tasks = if (tasks.isNotEmpty()) tasks.toEntities() else entity.tasks
            }
            .toDto()
}