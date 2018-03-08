package com.tcibinan.flaxo.model.entity

import com.tcibinan.flaxo.model.data.StudentTask
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Student task entity object.
 */
@Entity(name = "student_task")
@Table(name = "student_task")
class StudentTaskEntity : EntityObject<StudentTask> {

    @Id
    @GeneratedValue
    var studentTaskId: Long? = null
    @ManyToOne
    var task: TaskEntity? = null
    @ManyToOne
    var student: StudentEntity? = null
    var anyBuilds: Boolean = false
    var buildSucceed: Boolean = false

    override fun toDto() = StudentTask(this)
}