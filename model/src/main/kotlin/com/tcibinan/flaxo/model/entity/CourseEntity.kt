package com.tcibinan.flaxo.model.entity

import com.tcibinan.flaxo.model.data.Course
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
    var github_repository_id: String? = null
    @ManyToOne
    var user: UserEntity? = null
    @OneToMany(mappedBy = "course", orphanRemoval = true, fetch = FetchType.EAGER)
    var students: Set<StudentEntity> = emptySet()
    @OneToMany(mappedBy = "course", orphanRemoval = true, fetch = FetchType.EAGER)
    var tasks: Set<TaskEntity> = emptySet()

    override fun toDto(): Course = Course(this)
}