package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.Table

@Entity(name = "student_task")
@Table(name = "student_task")
class StudentTaskEntity() : ConvertibleEntity<StudentTask> {
    @Id @GeneratedValue
    var student_task_id: Long? = null
    var points: Int? = null
    @ManyToOne @JoinColumn(name = "task_id")
    var task: TaskEntity? = null
    @ManyToOne @JoinColumn(name = "student_id")
    var student: StudentEntity? = null

    constructor(student_task_id: Long, points: Int, task: TaskEntity, student: StudentEntity) : this() {
        this.student_task_id = student_task_id
        this.points = points
        this.task = task
        this.student = student
    }

    override fun toDto() = StudentTask(student_task_id!!, points!!, task!!.toDto(), student!!.toDto())
}

data class StudentTask(
        val studentTaskId: Long,
        val points: Int,
        val task: Task,
        val student: Student
) : DataObject<StudentTaskEntity> {
    override fun toEntity() = StudentTaskEntity(studentTaskId, points, task.toEntity(), student.toEntity())
}