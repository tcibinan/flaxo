package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.entity.StudentEntity

data class Student(
        val studentId: Long,
        val nickname: String,
        val studentTasks: Set<StudentTask>
) : DataObject<StudentEntity> {
    override fun toEntity() = StudentEntity(studentId, nickname, student_tasks = studentTasks.toEntities())
}