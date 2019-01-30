package org.flaxo.model.data

import org.flaxo.common.DateTime
import org.flaxo.model.CommitView
import java.time.LocalDateTime
import java.util.Objects
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

        // TODO 13.10.18: Rename to pullRequestNumber
        val pullRequestId: Int? = null,

        val date: LocalDateTime? = null,

        val sha: String = "",

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val solution: Solution = Solution()

) : Identifiable, Viewable<CommitView> {

    override fun view(): CommitView = CommitView(
            sha = sha,
            pullRequestId = pullRequestId,
            date = date?.let { DateTime(date) }
    )

    override fun toString() = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?) =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id
}
