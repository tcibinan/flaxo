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
        override val id: Long? = null,

        val taskName: String = "",

        val mossUrl: String? = null,

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val course: Course = Course(),

        @OneToMany(mappedBy = "task", orphanRemoval = true)
        val solutions: Set<Solution> = emptySet()
) : Viewable, Identifiable {

    override fun view(): Any = let { task ->
        object {
            val name = task.taskName
        }
    }

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?) = other is Task && other.id == id
}