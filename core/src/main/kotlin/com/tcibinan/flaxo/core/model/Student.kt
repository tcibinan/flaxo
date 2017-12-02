package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.Table

@Entity(name = "student")
@Table(name = "student")
class StudentEntity : ConvertibleEntity<Student> {
    @Id @GeneratedValue
    var student_id: Long? = null
    var nickname: String? = null
    @ManyToOne @JoinColumn(name = "course_id")
    var course: CourseEntity? = null

    constructor(nickname: String, course: CourseEntity) {
        this.nickname = nickname
        this.course = course
    }

    constructor(student_id: Long, nickname: String, course: CourseEntity) {
        this.student_id = student_id
        this.nickname = nickname
        this.course = course
    }

    override fun toDto() = Student(student_id!!, nickname!!, course!!.toDto())
}

data class Student(
        val studentId: Long,
        val nickname: String,
        val course: Course
) : DataObject<StudentEntity> {
    override fun toEntity() = StudentEntity(studentId, nickname, course.toEntity())
}