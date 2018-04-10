package com.tcibinan.flaxo.model.data

import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

/**
 * Student data object.
 */
@Entity(name = "student")
@Table(name = "student")
data class Student(
        @Id
        @GeneratedValue
        val studentId: Long? = null,

        val nickname: String = "",

        @ManyToOne
        val course: Course = Course(),

        @OneToMany(mappedBy = "student", orphanRemoval = true, fetch = FetchType.EAGER)
        val solutions: Set<Solution> = emptySet()
) : Viewable {

    override fun view(): Any = let { student ->
        object {
            val name = student.nickname
        }
    }

    override fun hashCode() = Objects.hash(studentId)

    override fun equals(other: Any?) = other is Student && other.studentId == studentId
}