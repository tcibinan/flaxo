package org.flaxo.common.data

/**
 * Course state.
 */
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
