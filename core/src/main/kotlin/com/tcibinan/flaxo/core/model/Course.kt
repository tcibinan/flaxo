package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.Table

@Entity(name = "course")
@Table(name = "course")
class CourseEntity {
    @Id @GeneratedValue
    var course_id: Long? = null
    var name: String? = null
    var language: String? = null
    var test_language: String? = null
    var testing_framework: String? = null
    @ManyToOne @JoinColumn(name = "user_id")
    var user: UserEntity? = null
}

data class Course(
        val courseId: Long,
        val name: String,
        val language: String,
        val testLanguage: String,
        val testingFramework: String,
        val user: User
)