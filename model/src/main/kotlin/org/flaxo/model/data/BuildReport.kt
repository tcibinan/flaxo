package org.flaxo.model.data

import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity(name = "build_report")
@Table(name = "build_report")
data class BuildReport(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        override val date: LocalDateTime = LocalDateTime.MIN,

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val solution: Solution = Solution(),

        val succeed: Boolean = false

) : Identifiable, Report, Viewable {

    override fun view(): Any = let { report ->
        object {
            val succeed = report.succeed
            val date = report.date
        }
    }

    override fun toString(): String = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id

}