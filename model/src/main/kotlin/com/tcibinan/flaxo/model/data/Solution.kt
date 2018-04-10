package com.tcibinan.flaxo.model.data

import java.util.*
import javax.persistence.Entity
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
        val solutionId: Long? = null,

        @ManyToOne
        val task: Task = Task(),

        @ManyToOne
        val student: Student = Student(),

        val anyBuilds: Boolean = false,

        val buildSucceed: Boolean = false,

        val grade: String = "B",

        val deadline: Boolean = true
) : Viewable {

    override fun view(): Any = let { studentTask ->
        object {
            val id = studentTask.solutionId
            val task = studentTask.task.taskName
            val student = studentTask.student.nickname
            val built = studentTask.anyBuilds
            val succeed = studentTask.buildSucceed
        }
    }

    override fun hashCode() = Objects.hash(solutionId)

    override fun equals(other: Any?) = other is Solution && other.solutionId == solutionId
}