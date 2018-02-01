package com.tcibinan.flaxo.model.entity

import com.tcibinan.flaxo.model.data.Student
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity(name = "student")
@Table(name = "student")
class StudentEntity() : ConvertibleEntity<Student> {

    @Id
    @GeneratedValue
    var student_id: Long? = null
    var nickname: String? = null
    @ManyToOne
    var course: CourseEntity? = null
    @OneToMany(mappedBy = "student", orphanRemoval = true, fetch = FetchType.EAGER)
    var student_tasks: Set<StudentTaskEntity> = emptySet()

    constructor(student_id: Long? = null,
                nickname: String,
                course: CourseEntity? = null,
                student_tasks: Set<StudentTaskEntity> = emptySet()
    ) : this() {
        this.student_id = student_id
        this.nickname = nickname
        this.course = course
        this.student_tasks = student_tasks
    }

    override fun toDto() = Student(student_id!!, nickname!!, student_tasks.toDtos())
}