package org.flaxo.common.data

import kotlinx.serialization.Serializable
import org.flaxo.common.Dated
import org.flaxo.common.Identifiable
import org.flaxo.common.Named

/**
 * Flaxo educational course.
 *
 * Each course is associated with a git repository.
 */
@Serializable
data class Course(

        override val id: Long,

        override val name: String,

        override val date: DateTime,

        /**
         * Course description.
         *
         * Only visible for course author.
         */
        val description: String?,

        /**
         * Course settings.
         */
        val settings: CourseSettings,

        /**
         * Git repository url.
         */
        val url: String,

        /**
         * Course state.
         */
        val state: CourseState,

        /**
         * Owner of the course.
         */
        val user: User,

        /**
         * Student nicknames.
         */
        val students: List<String>,

        /**
         * Course tasks (branches) names.
         */
        val tasks: List<String>
) : Identifiable, Named, Dated
