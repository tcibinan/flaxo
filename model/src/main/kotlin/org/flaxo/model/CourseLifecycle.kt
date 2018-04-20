package org.flaxo.model

/**
 * Interface represents course course lifecycle state.
 *
 * It could be [INIT]. In that case course hasn't started yet.
 * The owner of the course is filling it with tasks and tests.
 *
 * It could be [RUNNING]. In that case course has already started.
 * Students could send pull requests, all services and web hooks
 * have been set up.
 *
 * It could be [CLOSED]. In that case course has been closed.
 * All results freezes and students doesn't have an opportunity
 * to send new pull requests, all services and web hooks have
 * been stopped for the original course.
 */
enum class CourseLifecycle {
    INIT,
    RUNNING,
    CLOSED
}