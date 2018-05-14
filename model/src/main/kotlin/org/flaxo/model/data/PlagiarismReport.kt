package org.flaxo.model.data

import java.time.LocalDateTime
import java.util.*
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

/**
 * Plagiarism report entity.
 */
@Entity(name = "plagiarism_report")
@Table(name = "plagiarism_report")
data class PlagiarismReport(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        override val date: LocalDateTime = LocalDateTime.MIN,

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val task: Task = Task(),

        val url: String = "",

        @OneToMany(cascade = [CascadeType.ALL])
        val matches: List<PlagiarismMatch> = mutableListOf()

) : Identifiable, Report, Viewable {

    override fun view(): Any = let { report ->
        object {
            val url = report.url
            val date = report.date
            val matches = report.matches.views()
        }
    }

    override fun toString(): String = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id
}