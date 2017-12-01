package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn

@Entity
data class StudentTask(
        @Id @GeneratedValue
        val student_task_id: Long,
        val points: Int,
        @ManyToOne @JoinColumn(name = "task_id")
        val task: Task,
        @ManyToOne @JoinColumn(name = "student_id")
        val student: Student
)