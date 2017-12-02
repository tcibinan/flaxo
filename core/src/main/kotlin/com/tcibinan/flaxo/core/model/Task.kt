package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.Table

@Entity(name = "task")
@Table(name = "task")
class TaskEntity {
    @Id @GeneratedValue
    var task_id: Long? = null
    var task_name: String? = null
    @ManyToOne @JoinColumn(name = "course_id")
    var course: CourseEntity? = null
}

data class Task(
        val taskId: Long,
        val taskName: String,
        val course: CourseEntity
)