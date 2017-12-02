package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity(name = "task")
@Table(name = "task")
class TaskEntity() : ConvertibleEntity<Task> {
    @Id @GeneratedValue
    var task_id: Long? = null
    var task_name: String? = null
    @ManyToOne @JoinColumn(name = "course_id")
    var course: CourseEntity? = null
    @OneToMany(mappedBy = "task_id")
    var student_tasks: Set<StudentTaskEntity> = emptySet()

    constructor(task_id: Long? = null,
                task_name: String,
                course: CourseEntity,
                student_tasks: Set<StudentTaskEntity> = emptySet()) : this() {
        this.task_id = task_id
        this.task_name = task_name
        this.course = course
        this.student_tasks = student_tasks
    }

    override fun toDto() = Task(task_id!!, task_name!!, course!!.toDto(), student_tasks.toDtos())
}

data class Task(
        val taskId: Long,
        val taskName: String,
        val course: Course,
        val studentTasks: Set<StudentTask> = emptySet()
) : DataObject<TaskEntity> {
    override fun toEntity() = TaskEntity(taskId, taskName, course.toEntity(), studentTasks.toEntities())
}