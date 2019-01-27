package org.flaxo.common.data

/**
 * Course state.
 */
class CourseState(
        /**
         * Course lifecycle status.
         */
        val lifecycle: CourseLifecycle,

        /**
         * Course activated external services.
         */
        val activatedServices: List<ExternalService>
)