package org.flaxo.common.data

import org.flaxo.common.DateTime

/**
 * Flaxo educational course.
 *
 * Each course is associated with a git repository.
 */
class Course(
        /**
         * Course and repository name.
         */
        val name: String,

        /**
         * Course description.
         *
         * Only visible for course author.
         */
        val description: String?,

        /**
         * Course creation date time.
         */
        val createdDate: DateTime,

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
)