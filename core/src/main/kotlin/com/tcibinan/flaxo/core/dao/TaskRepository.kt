package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.entity.CourseEntity
import com.tcibinan.flaxo.core.entity.TaskEntity
import org.springframework.data.repository.CrudRepository

interface TaskRepository : CrudRepository<TaskEntity, Long> {
    fun findAllByCourse(course: CourseEntity): Set<TaskEntity>
}