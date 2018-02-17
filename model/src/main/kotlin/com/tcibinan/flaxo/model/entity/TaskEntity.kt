package com.tcibinan.flaxo.model.entity

import com.tcibinan.flaxo.model.data.Task
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity(name = "task")
@Table(name = "task")
class TaskEntity() : ConvertibleEntity<Task> {

    @Id
    @GeneratedValue
    var task_id: Long? = null
    var task_name: String? = null
    @ManyToOne
    var course: CourseEntity? = null
    @OneToMany(mappedBy = "task", orphanRemoval = true, fetch = FetchType.EAGER)
    var student_tasks: Set<StudentTaskEntity> = emptySet()

    override fun toDto() = Task(this)
}