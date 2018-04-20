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
 * Course state data object.
 */
@Entity(name = "course_state")
@Table(name = "course_state")
data class CourseState(
        @Id
        @GeneratedValue
        override val id: Long = -1,

        val lifecycle: CourseLifecycle = CourseLifecycle.INIT,

        @ElementCollection(fetch = FetchType.EAGER)
        val activatedServices: List<IntegratedService> = emptyList()
) : Viewable, Identifiable {

    override fun view(): Any = let { state ->
        object {
            val id = state.id
            val lifecycle = state.lifecycle
            val activatedServices = state.activatedServices
        }
    }

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?) = other is CourseState && other.id == id
}