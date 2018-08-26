package org.flaxo.model.data

import org.flaxo.model.PlagiarismMatchView
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

) : Identifiable, Viewable<PlagiarismMatchView> {

    override fun view(): PlagiarismMatchView = PlagiarismMatchView(
            url = url,
            student1 = student1,
            student2 = student2,
            lines = lines,
            percentage = percentage
    )

    override fun toString() = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?) =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id
}