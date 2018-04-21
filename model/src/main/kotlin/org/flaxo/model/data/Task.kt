package org.flaxo.model.data

import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.Table

/**
 * Task data object.
 */
@Entity(name = "task")
@Table(name = "task")
data class Task(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        val branch: String = "",

        @OneToOne(fetch = FetchType.LAZY)
        val plagiarismReport: PlagiarismReport? = null,

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val course: Course = Course(),

        @OneToMany(mappedBy = "task", orphanRemoval = true)
        val solutions: Set<Solution> = emptySet()

) : Identifiable, Viewable {

    override fun view(): Any = let { task ->
        object {
            val branch = task.branch
            val plagiarismReport = task.plagiarismReport?.view()
            val solutions = task.solutions.views()
        }
    }

    override fun toString(): String = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id
}