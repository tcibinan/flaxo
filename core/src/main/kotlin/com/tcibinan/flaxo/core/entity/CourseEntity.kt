package com.tcibinan.flaxo.core.entity

import com.tcibinan.flaxo.core.model.Course
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity(name = "course")
@Table(name = "course")
class CourseEntity() : ConvertibleEntity<Course> {
    @Id
    @GeneratedValue
    var course_id: Long? = null
    var name: String? = null
    var language: String? = null
    var test_language: String? = null
    var testing_framework: String? = null
    var status: String? = null
    @ManyToOne
    var user: UserEntity? = null
    @OneToMany(mappedBy = "course", orphanRemoval = true, fetch = FetchType.EAGER)
    var students: Set<StudentEntity> = emptySet()
    @OneToMany(mappedBy = "course", orphanRemoval = true, fetch = FetchType.EAGER)
    var tasks: Set<TaskEntity> = emptySet()

    constructor(course_id: Long? = null,
                name: String,
                language: String,
                test_language: String,
                testing_framework: String,
                status: String,
                user: UserEntity,
                students: Set<StudentEntity> = emptySet(),
                tasks: Set<TaskEntity> = emptySet()
    ) : this() {
        this.course_id = course_id
        this.name = name
        this.language = language
        this.test_language = test_language
        this.testing_framework = testing_framework
        this.status = status
        this.user = user
        this.students = students
        this.tasks = tasks
    }

    override fun toDto() =
            Course(
                    course_id!!,
                    name!!,
                    language!!,
                    test_language!!,
                    testing_framework!!,
                    status!!,
                    user!!.toDto(),
                    students.toDtos(),
                    tasks.toDtos()
            )
}