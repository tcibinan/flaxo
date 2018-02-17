package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.EntityFieldIsAbsent
import com.tcibinan.flaxo.model.entity.StudentEntity
import com.tcibinan.flaxo.model.entity.toDtos

data class Student(private val entity: StudentEntity) : DataObject<StudentEntity> {

    val id: Long by lazy { entity.student_id ?: throw EntityFieldIsAbsent("student", "id") }
    val nickname: String by lazy { entity.nickname ?: throw EntityFieldIsAbsent("student", "nickname") }
    val course: Course by lazy { Course(entity.course ?: throw EntityFieldIsAbsent("student", "course")) }
    val studentTasks: Set<StudentTask> by lazy { entity.student_tasks.toDtos() }

    override fun toEntity() = entity
}