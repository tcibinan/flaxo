package com.tcibinan.flaxo.model.dao

import com.tcibinan.flaxo.model.data.Course
import com.tcibinan.flaxo.model.data.Task
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for task entity.
 */
interface TaskRepository : CrudRepository<Task, Long> {
    fun findAllByCourse(course: Course): Set<Task>
}