package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity(name = "student")
@Table(name = "student")
class StudentEntity() : ConvertibleEntity<Student> {
    @Id @GeneratedValue
    var student_id: Long? = null
    var nickname: String? = null
    @ManyToOne @JoinColumn(name = "course_id")
    var course: CourseEntity? = null
    @OneToMany(mappedBy = "student_id")
    var student_tasks: Set<StudentTaskEntity> = emptySet()

    constructor(student_id: Long? = null,
                nickname: String,
                course: CourseEntity,
                student_tasks: Set<StudentTaskEntity> = emptySet()) : this() {
        this.student_id = student_id
        this.nickname = nickname
        this.course = course
        this.student_tasks = student_tasks
    }

    override fun toDto() = Student(student_id!!, nickname!!, course!!.toDto(), student_tasks.toDtos())
}

data class Student(
        val studentId: Long,
        val nickname: String,
        val course: Course,
        val studentTasks: Set<StudentTask> = emptySet()
) : DataObject<StudentEntity> {
    override fun toEntity() = StudentEntity(studentId, nickname, course.toEntity(), studentTasks.toEntities())
}