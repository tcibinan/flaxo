package org.flaxo.model.data

import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity(name = "code_style_report")
@Table(name = "code_style_report")
data class CodeStyleReport(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        override val date: LocalDateTime = LocalDateTime.MIN,

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val solution: Solution = Solution(),

        val grade: String = "F"

) : Identifiable, Report, Viewable {

    override fun view(): Any = let { report ->
        object {
            val grade = report.grade
            val date = report.date
        }
    }

    override fun toString(): String = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id
}
