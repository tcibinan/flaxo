package org.flaxo.model.data

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

/**
 * Plagiarism match entity.
 */
@Entity(name = "plagiarism_match")
@Table(name = "plagiarism_match")
data class PlagiarismMatch(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        val student1: String = "",

        val student2: String = "",

        val lines: Int = 0,

        val url: String = "",

        val percentage: Int = 0

) : Identifiable, Viewable {

    override fun view(): Any = let { match ->
        object {
            val url: String = match.url
            val student1: String = match.student1
            val student2: String = match.student2
            val lines: Int = match.lines
            val percentage: Int = match.percentage
        }
    }

    override fun toString(): String = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id
}