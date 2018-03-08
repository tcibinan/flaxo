package com.tcibinan.flaxo.model.entity

import com.tcibinan.flaxo.model.data.Task
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

/**
 * Task entity object.
 */
@Entity(name = "task")
@Table(name = "task")
class TaskEntity : EntityObject<Task> {

    @Id
    @GeneratedValue
    var taskId: Long? = null
    var taskName: String? = null
    var mossUrl: String? = null
    @ManyToOne
    var course: CourseEntity? = null
    @OneToMany(mappedBy = "task", orphanRemoval = true, fetch = FetchType.EAGER)
    var studentTasks: Set<StudentTaskEntity> = emptySet()

    override fun toDto() = Task(this)
}