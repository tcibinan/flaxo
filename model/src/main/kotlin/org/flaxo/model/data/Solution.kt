package org.flaxo.model.data

import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

/**
 * Solution entity.
 */
@Entity(name = "solution")
@Table(name = "solution")
data class Solution(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val task: Task = Task(),

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val student: Student = Student(),

        @OneToMany(mappedBy = "solution", orphanRemoval = true)
        val commits: List<Commit> = mutableListOf(),

        @OneToMany(mappedBy = "solution", orphanRemoval = true)
        val buildReports: List<BuildReport> = mutableListOf(),

        @OneToMany(mappedBy = "solution", orphanRemoval = true)
        val codeStyleReports: List<CodeStyleReport> = mutableListOf(),

        val score: Int? = null

) : Identifiable, Viewable {

    override fun view(): Any = let { solution ->
        object {
            val task = solution.task.branch
            val student = solution.student.nickname
            val score = solution.score
            val commits = solution.commits
            val buildReports = solution.buildReports.views()
            val codeStyleReports = solution.codeStyleReports.views()
        }
    }

    override fun toString(): String = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id
}