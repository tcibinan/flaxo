package com.tcibinan.flaxo.model.data

import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Student task data object.
 */
@Entity(name = "student_task")
@Table(name = "student_task")
data class Solution(
        @Id
        @GeneratedValue
        override val id: Long? = null,

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val task: Task = Task(),

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val student: Student = Student(),

        val built: Boolean = false,

        val succeed: Boolean = false,

        val grade: String? = null,

        val deadline: Boolean = true
) : Viewable, Identifiable {

    override fun view(): Any = let { solution ->
        object {
            val id = solution.id
            val task = solution.task.taskName
            val student = solution.student.nickname
            val built = solution.built
            val succeed = solution.succeed
            val grade = solution.grade
            val deadline = solution.deadline
        }
    }

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?) = other is Solution && other.id == id
}