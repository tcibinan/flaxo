package org.flaxo.model.dao

import org.flaxo.model.data.Course
import org.flaxo.model.data.Task
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for task entity.
 */
interface TaskRepository : CrudRepository<Task, Long> {

    /**
     * Finds all [course] tasks.
     */
    fun findAllByCourse(course: Course): Set<Task>
}