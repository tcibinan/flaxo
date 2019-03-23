package org.flaxo.common.data

import kotlinx.serialization.Serializable

/**
 * Course statistics.
 */
@Serializable
data class CourseStatistics(

        /**
         * Task statistics.
         */
        val tasks: List<Task>
)
