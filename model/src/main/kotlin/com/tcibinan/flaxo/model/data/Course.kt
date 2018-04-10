package com.tcibinan.flaxo.model.data

import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Course data object.
 */
@Entity(name = "course")
@Table(name = "course")
data class Course(
        @Id
        @GeneratedValue
        val courseId: Long? = null,

        val name: String = "",

        val language: String = "",

        val testingLanguage: String = "",

        val testingFramework: String = "",

        val status: String = "",

        @ManyToOne
        val user: User = User(),

        @OneToMany(mappedBy = "course", orphanRemoval = true, fetch = FetchType.EAGER)
        val students: Set<Student> = emptySet(),

        @OneToMany(mappedBy = "course", orphanRemoval = true, fetch = FetchType.EAGER)
        val tasks: Set<Task> = emptySet()
) : Viewable {

    override fun view(): Any = let { course ->
        object {
            val id = course.courseId
            val name = course.name
            val language = course.language
            val testingLanguage = course.testingLanguage
            val testingFramework = course.testingFramework
            val status = course.status
            val user = course.user.nickname
            val userGithubId = course.user.githubId
            val students = course.students.map { it.nickname }
            val tasks = course.tasks.map { it.taskName }
        }
    }

    override fun hashCode() = Objects.hash(courseId)

    override fun equals(other: Any?) = other is Course && other.courseId == courseId
}