package org.flaxo.model.data

import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Task entity.
 */
@Entity(name = "task")
@Table(name = "task")
data class Task(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        val branch: String = "",

        val url: String = "",

        val deadline: LocalDateTime? = null,

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val course: Course = Course(),

        @OneToMany(mappedBy = "task", orphanRemoval = true)
        val plagiarismReports: List<PlagiarismReport> = mutableListOf(),

        @OneToMany(mappedBy = "task", orphanRemoval = true)
        val solutions: Set<Solution> = mutableSetOf()

) : Identifiable, Viewable {

    override fun view(): Any = let { task ->
        object {
            val branch = task.branch
            val url = task.url
            val deadline = task.deadline
            val plagiarismReports = task.plagiarismReports.views()
            val solutions = task.solutions.views()
        }
    }

    override fun toString(): String = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id
}