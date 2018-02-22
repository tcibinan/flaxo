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
    var studentId: Long? = null
    var nickname: String? = null
    @ManyToOne
    var course: CourseEntity? = null
    @OneToMany(mappedBy = "student", orphanRemoval = true, fetch = FetchType.EAGER)
    var studentTasks: Set<StudentTaskEntity> = emptySet()

    override fun toDto() = Student(this)
}