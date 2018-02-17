package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.EntityFieldIsAbsent
import com.tcibinan.flaxo.model.entity.StudentTaskEntity

data class StudentTask(private val entity: StudentTaskEntity) : DataObject<StudentTaskEntity> {

    val id: Long by lazy { entity.student_task_id ?: throw EntityFieldIsAbsent("student task", "id") }
    val task: Task by lazy { Task(entity.task ?: throw EntityFieldIsAbsent("student task", "task")) }
    val student: Student by lazy { Student(entity.student ?: throw EntityFieldIsAbsent("student task", "student")) }
    val points: Int by lazy { entity.points }

    override fun toEntity() = entity
}