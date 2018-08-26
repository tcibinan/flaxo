package org.flaxo.common

/**
 * Course lifecycle status.
 */
enum class CourseLifecycle {

    /**
     * In that case course hasn't started yet.
     * The owner of the course is filling it with tasks and tests.
     */
    INIT,

    /**
     * In that case course has already started.
     * Students send pull requests, all services and web hooks
     * have been set up.
     */
    RUNNING,

    /**
     * In that case course has been closed.
     * All results are frozen and new pull requests are ignored,
     * all integrated services have been stopped.
     */
    CLOSED
}
