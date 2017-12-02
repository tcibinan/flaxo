package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.Table

@Entity(name = "student")
@Table(name = "student")
class StudentEntity {
    @Id @GeneratedValue
    var student_id: Long? = null
    var nickname: String? = null
    @ManyToOne @JoinColumn(name = "course_id")
    var course: CourseEntity? = null
}

data class Student(
        val studentId: Long,
        val nickname: String,
        val course: Course
)