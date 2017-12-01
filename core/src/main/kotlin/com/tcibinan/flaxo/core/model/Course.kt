package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn

@Entity
data class Course(
        @Id @GeneratedValue
        val course_id: Long,
        val name: String,
        val language: String,
        val test_language: String,
        val testing_framework: String,
        @ManyToOne @JoinColumn(name = "user_id")
        val user: User
)