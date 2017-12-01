package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn

@Entity
data class Student(
        @Id @GeneratedValue
        val student_id: Long,
        val nickname: String,
        @ManyToOne @JoinColumn(name = "course_id")
        val course: Course
)