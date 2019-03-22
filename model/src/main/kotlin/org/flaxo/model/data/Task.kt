package org.flaxo.model.data

import org.flaxo.common.DateTime
import org.flaxo.model.TaskView
import java.time.LocalDateTime
import java.util.Objects
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
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

) : Identifiable, Viewable<TaskView> {

    override fun view(): TaskView = TaskView(
            id = id,
            branch = branch,
            url = url,
            deadline = deadline?.let { DateTime(it) },
            plagiarismReports = plagiarismReports.views(),
            solutions = solutions.views()
    )

    override fun toString() = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean = this::class.isInstance(other) && other is Identifiable && other.id == id

}