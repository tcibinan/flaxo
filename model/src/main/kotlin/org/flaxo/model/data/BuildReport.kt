package org.flaxo.model.data

import org.flaxo.common.DateTime
import org.flaxo.model.BuildReportView
import java.time.LocalDateTime
import java.util.Objects
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Build report entity.
 */
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

) : Identifiable, Report, Viewable<BuildReportView> {

    override fun view(): BuildReportView = BuildReportView(
            date = DateTime(date),
            succeed = succeed
    )

    override fun toString() = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean = this::class.isInstance(other) && other is Identifiable && other.id == id

}