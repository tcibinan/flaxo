package com.tcibinan.flaxo.model

/**
 * Interface represents course status.
 *
 * It could be [INIT]. In that case course hasn't started yet.
 * The owner of the course is filling it with tasks and tests.
 *
 * It could be [RUNNING]. In that case course has already started.
 * Students could send pull requests, all services and web hooks
 * have been set up.
 */
interface CourseStatus {
    companion object {
        val INIT = "init"
        val RUNNING = "running"
    }
}