package com.tcibinan.flaxo.core.model

import com.tcibinan.flaxo.core.entity.TaskEntity

data class Task(
        val taskId: Long,
        val taskName: String,
        val studentTasks: Set<StudentTask>
) : DataObject<TaskEntity> {
    override fun toEntity() = TaskEntity(taskId, taskName, student_tasks = studentTasks.toEntities())
}