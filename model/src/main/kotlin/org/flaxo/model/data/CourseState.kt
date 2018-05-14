package org.flaxo.model.data

import org.flaxo.model.CourseLifecycle
import org.flaxo.model.IntegratedService
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
        val activatedServices: Set<IntegratedService> = mutableSetOf()

) : Identifiable, Viewable {

    override fun view(): Any = let { state ->
        object {
            val lifecycle = state.lifecycle
            val activatedServices = state.activatedServices
        }
    }

    override fun toString(): String = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id
}