package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.entity.StudentTaskEntity

/**
 * Student task data object.
 */
data class StudentTask(private val entity: StudentTaskEntity)
    : DataObject<StudentTaskEntity> {

    val id: Long by lazy { entity.studentTaskId ?: missing("id") }
    val task: Task by lazy { Task(entity.task ?: missing("task")) }
    val student: Student by lazy { Student(entity.student ?: missing("student")) }
    val anyBuilds: Boolean by lazy { entity.anyBuilds }
    val buildSucceed: Boolean by lazy { entity.buildSucceed }

    override fun toEntity() = entity

    override fun view(): Any = let { studentTask ->
        object {
            val id = studentTask.id
            val task = studentTask.task.name
            val student = studentTask.student.nickname
            val built = studentTask.anyBuilds
            val succeed = studentTask.buildSucceed
        }
    }

    fun with(id: Long? = null,
             task: Task? = null,
             student: Student? = null,
             anyBuilds: Boolean? = null,
             buildSucceed: Boolean? = null
    ): StudentTask = StudentTaskEntity()
            .apply {
                this.studentTaskId = id ?: entity.studentTaskId
                this.task = task?.toEntity() ?: entity.task
                this.student = student?.toEntity() ?: entity.student
                this.anyBuilds = anyBuilds ?: entity.anyBuilds
                this.buildSucceed = buildSucceed ?: entity.buildSucceed
            }
            .toDto()
}