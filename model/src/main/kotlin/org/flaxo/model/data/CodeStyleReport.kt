package org.flaxo.model.data

import org.flaxo.common.DateTime
import org.flaxo.common.data.CodeStyleGrade
import org.flaxo.model.CodeStyleReportView
import java.time.LocalDateTime
import java.util.Objects
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Code style report entity.
 */
@Entity(name = "code_style_report")
@Table(name = "code_style_report")
data class CodeStyleReport(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        override val date: LocalDateTime = LocalDateTime.MIN,

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val solution: Solution = Solution(),

        val grade: CodeStyleGrade = CodeStyleGrade.F

) : Identifiable, Report, Viewable<CodeStyleReportView> {

    override fun view(): CodeStyleReportView = CodeStyleReportView(
            id = id,
            date = DateTime(date),
            grade = grade
    )

    override fun toString(): String = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean = this::class.isInstance(other) && other is Identifiable && other.id == id

}
