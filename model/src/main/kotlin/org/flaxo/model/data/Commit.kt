package org.flaxo.model.data

import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Solution commit entity.
 */
@Entity(name = "commit")
@Table(name = "commit")
class Commit(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        val pullRequestId: Int? = null,

        val date: LocalDateTime? = null,

        val sha: String = "",

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val solution: Solution = Solution()

) : Identifiable, Viewable {

    override fun view() = let { commit ->
        object {
            val pullRequestId = commit.pullRequestId
            val sha = commit.sha
            val date = commit.date
        }
    }

    override fun toString(): String = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id
}
