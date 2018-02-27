package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.EntityFieldIsAbsent
import com.tcibinan.flaxo.model.entity.StudentTaskEntity

data class StudentTask(private val entity: StudentTaskEntity) : DataObject<StudentTaskEntity> {

    val id: Long by lazy { entity.studentTaskId ?: throw EntityFieldIsAbsent("student task", "id") }
    val task: Task by lazy { Task(entity.task ?: throw EntityFieldIsAbsent("student task", "task")) }
    val student: Student by lazy { Student(entity.student ?: throw EntityFieldIsAbsent("student task", "student")) }
    val anyBuilds: Boolean by lazy { entity.anyBuilds }
    val buildSucceed: Boolean by lazy { entity.buildSucceed }

    override fun toEntity() = entity
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