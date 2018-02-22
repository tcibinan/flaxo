package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.EntityFieldIsAbsent
import com.tcibinan.flaxo.model.entity.TaskEntity
import com.tcibinan.flaxo.model.entity.toDtos

data class Task(private val entity: TaskEntity) : DataObject<TaskEntity> {

    val id: Long by lazy { entity.taskId ?: throw EntityFieldIsAbsent("task", "id") }
    val name: String by lazy { entity.taskName ?: throw EntityFieldIsAbsent("task", "name") }
    val course: Course by lazy { Course(entity.course ?: throw EntityFieldIsAbsent("task", "course")) }
    val studentTasks: Set<StudentTask> by lazy { entity.studentTasks.toDtos() }

    override fun toEntity() = entity

    fun with(id: Long? = null,
             name: String? = null,
             course: Course? = null,
             studentTasks: Set<StudentTask> = emptySet()
    ): Task = TaskEntity()
            .apply {
                this.taskId = id ?: entity.taskId
                this.taskName = name ?: entity.taskName
                this.course = course?.toEntity() ?: entity.course
                this.studentTasks =
                        if (studentTasks.toEntities().isNotEmpty()) studentTasks.toEntities()
                        else entity.studentTasks
            }
            .toDto()

}