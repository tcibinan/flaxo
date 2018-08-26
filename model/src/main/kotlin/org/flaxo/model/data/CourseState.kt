package org.flaxo.model.data

import org.flaxo.common.CourseLifecycle
import org.flaxo.common.ExternalService
import org.flaxo.model.CourseStateView
import java.util.*
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

/**
 * Course state entity.
 */
@Entity(name = "course_state")
@Table(name = "course_state")
data class CourseState(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        val lifecycle: CourseLifecycle = CourseLifecycle.INIT,

        @ElementCollection(fetch = FetchType.EAGER)
        val activatedServices: Set<ExternalService> = mutableSetOf()

) : Identifiable, Viewable<CourseStateView> {

    override fun view(): CourseStateView = CourseStateView(
            lifecycle = lifecycle,
            activatedServices = activatedServices.toList()
    )

    override fun toString() = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?) =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id
}