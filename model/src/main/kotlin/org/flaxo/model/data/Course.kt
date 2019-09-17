package org.flaxo.model.data

import org.flaxo.common.Identifiable
import org.flaxo.common.Named
import org.flaxo.common.data.DateTime
import org.flaxo.model.CourseView
import java.time.LocalDateTime
import java.util.*
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Table

/**
 * Course entity.
 */
@Entity(name = "course")
@Table(name = "course")
data class Course(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        override val name: String = "",

        val description: String? = null,

        val private: Boolean = false,

        // TODO 23.03.19: Rename to date.
        val createdDate: LocalDateTime = LocalDateTime.MIN,

        @OneToOne(cascade = [CascadeType.ALL], optional = false, fetch = FetchType.LAZY)
        val settings: CourseSettings = CourseSettings(),

        val url: String = "",

        @OneToOne(cascade = [CascadeType.ALL], optional = false, fetch = FetchType.LAZY)
        val state: CourseState = CourseState(),

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val user: User = User(),

        @OneToMany(mappedBy = "course", orphanRemoval = true)
        val students: Set<Student> = mutableSetOf(),

        @OneToMany(mappedBy = "course", orphanRemoval = true)
        val tasks: Set<Task> = mutableSetOf()

) : Identifiable, Named, Viewable<CourseView> {

    override fun view(): CourseView = CourseView(
            id = id,
            name = name,
            description = description,
            private = private,
            date = DateTime(createdDate),
            settings = settings.view(),
            state = state.view(),
            user = user.view(),
            url = url,
            students = students.map { it.nickname },
            tasks = tasks.map { it.branch }
    )

    override fun toString() = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean = this::class.isInstance(other) && other is Identifiable && other.id == id

}
