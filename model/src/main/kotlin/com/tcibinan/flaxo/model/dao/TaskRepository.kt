package com.tcibinan.flaxo.model.dao

import com.tcibinan.flaxo.model.entity.CourseEntity
import com.tcibinan.flaxo.model.entity.TaskEntity
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for task entity.
 */
interface TaskRepository : CrudRepository<TaskEntity, Long> {
    fun findAllByCourse(course: CourseEntity): Set<TaskEntity>
}