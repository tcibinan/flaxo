package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn

@Entity
data class Task(
        @Id @GeneratedValue
        val task_id: Long,
        val task_name: String,
        @ManyToOne @JoinColumn(name = "course_id")
        val course: Course
)