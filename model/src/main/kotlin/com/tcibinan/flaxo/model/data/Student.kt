package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.EntityFieldIsAbsent
import com.tcibinan.flaxo.model.entity.StudentEntity
import com.tcibinan.flaxo.model.entity.toDtos

/**
 * Student data object.
 */
data class Student(private val entity: StudentEntity)
    : DataObject<StudentEntity> {

    val id: Long by lazy { entity.studentId ?: throw EntityFieldIsAbsent("student", "id") }
    val nickname: String by lazy { entity.nickname ?: throw EntityFieldIsAbsent("student", "nickname") }
    val course: Course by lazy { Course(entity.course ?: throw EntityFieldIsAbsent("student", "course")) }
    val studentTasks: Set<StudentTask> by lazy { entity.studentTasks.toDtos() }

    override fun toEntity() = entity
}