package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.Table

@Entity(name = "course")
@Table(name = "course")
class CourseEntity() : ConvertibleEntity<Course> {
    @Id @GeneratedValue
    var course_id: Long? = null
    var name: String? = null
    var language: String? = null
    var test_language: String? = null
    var testing_framework: String? = null
    @ManyToOne @JoinColumn(name = "user_id")
    var user: UserEntity? = null

    constructor(course_id: Long? = null,
                name: String,
                language: String,
                test_language: String,
                testing_framework: String,
                user: UserEntity) : this() {
        this.course_id = course_id
        this.name = name
        this.language = language
        this.test_language = test_language
        this.testing_framework = testing_framework
        this.user = user
    }

    override fun toDto() =
            Course(course_id!!, name!!, language!!, test_language!!, testing_framework!!, user!!.toDto())
}

data class Course(
        val courseId: Long,
        val name: String,
        val language: String,
        val testLanguage: String,
        val testingFramework: String,
        val user: User
) : DataObject<CourseEntity> {
    override fun toEntity() = CourseEntity(courseId, name, language, testLanguage, testingFramework, user.toEntity())
}