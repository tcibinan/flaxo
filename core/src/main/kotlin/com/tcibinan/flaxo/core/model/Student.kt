package com.tcibinan.flaxo.core.model

import com.tcibinan.flaxo.core.entity.StudentEntity

data class Student(
        val studentId: Long,
        val nickname: String,
        val studentTasks: Set<StudentTask>
) : DataObject<StudentEntity> {
    override fun toEntity() = StudentEntity(studentId, nickname, student_tasks = studentTasks.toEntities())
}