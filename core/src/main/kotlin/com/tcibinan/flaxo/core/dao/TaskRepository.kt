package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.model.CourseEntity
import com.tcibinan.flaxo.core.model.TaskEntity
import org.springframework.data.repository.CrudRepository

interface TaskRepository : CrudRepository<TaskEntity, Long> {
    fun findAllByCourse(course: CourseEntity): Set<TaskEntity>
}