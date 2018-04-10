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
 * Task data object.
 */
@Entity(name = "task")
@Table(name = "task")
data class Task(
        @Id
        @GeneratedValue
        val taskId: Long? = null,

        val taskName: String = "",

        val mossUrl: String? = null,

        @ManyToOne
        val course: Course = Course(),

        @OneToMany(mappedBy = "task", orphanRemoval = true, fetch = FetchType.EAGER)
        val solutions: Set<Solution> = emptySet()
) : Viewable {

    override fun view(): Any = let { task ->
        object {
            val name = task.taskName
        }
    }

    override fun hashCode() = Objects.hash(taskId)

    override fun equals(other: Any?) = other is Task && other.taskId == taskId
//
//    override fun toString() =
//            "Task(" +
//                    "taskId=$taskId, " +
//                    "taskName='$taskName', " +
//                    "mossUrl=$mossUrl, " +
//                    "solutions=${solutions.size}" +
//                    ")"
}