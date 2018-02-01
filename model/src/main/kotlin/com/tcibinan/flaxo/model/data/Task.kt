package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.entity.TaskEntity

data class Task(
        val taskId: Long,
        val taskName: String,
        val studentTasks: Set<StudentTask>
) : DataObject<TaskEntity> {
    override fun toEntity() = TaskEntity(taskId, taskName, student_tasks = studentTasks.toEntities())
}