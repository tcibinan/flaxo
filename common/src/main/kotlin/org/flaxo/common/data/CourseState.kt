package org.flaxo.common.data

import kotlinx.serialization.Serializable
import org.flaxo.common.Identifiable

/**
 * Course state.
 */
@Serializable
data class CourseState(

        override val id: Long,

        /**
         * Course lifecycle status.
         */
        val lifecycle: CourseLifecycle,

        /**
         * Course activated external services.
         */
        val activatedServices: List<ExternalService>
): Identifiable
