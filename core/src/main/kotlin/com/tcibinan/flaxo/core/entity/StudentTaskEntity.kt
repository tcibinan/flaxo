package com.tcibinan.flaxo.core.entity

import com.tcibinan.flaxo.core.model.StudentTask
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity(name = "student_task")
@Table(name = "student_task")
class StudentTaskEntity() : ConvertibleEntity<StudentTask> {
    @Id
    @GeneratedValue
    var student_task_id: Long? = null
    @ManyToOne
    var task: TaskEntity? = null
    @ManyToOne
    var student: StudentEntity? = null
    var points: Int = 0

    constructor(student_task_id: Long? = null,
                task: TaskEntity? = null,
                student: StudentEntity? = null,
                points: Int = 0
    ) : this() {
        this.student_task_id = student_task_id
        this.task = task
        this.student = student
        this.points = points
    }

    override fun toDto() = StudentTask(student_task_id!!, points)
}