package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.Table

@Entity(name = "task")
@Table(name = "task")
class TaskEntity : ConvertibleEntity<Task> {
    @Id @GeneratedValue
    var task_id: Long? = null
    var task_name: String? = null
    @ManyToOne @JoinColumn(name = "course_id")
    var course: CourseEntity? = null

    constructor(task_name: String, course: CourseEntity) {
        this.task_name = task_name
        this.course = course
    }

    constructor(task_id: Long, task_name: String, course: CourseEntity) {
        this.task_id = task_id
        this.task_name = task_name
        this.course = course
    }

    override fun toDto() = Task(task_id!!, task_name!!, course!!.toDto())
}

data class Task(
        val taskId: Long,
        val taskName: String,
        val course: Course
) : DataObject<TaskEntity> {
    override fun toEntity() = TaskEntity(taskId, taskName, course.toEntity())
}