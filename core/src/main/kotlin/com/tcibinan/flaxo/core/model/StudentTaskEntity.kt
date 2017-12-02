package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.Table

@Entity(name = "student_task")
@Table(name = "student_task")
class StudentTaskEntity {
    @Id @GeneratedValue
    var student_task_id: Long? = null
    var points: Int? = null
    @ManyToOne @JoinColumn(name = "task_id")
    var task: TaskEntity? = null
    @ManyToOne @JoinColumn(name = "student_id")
    var student: StudentEntity? = null
}