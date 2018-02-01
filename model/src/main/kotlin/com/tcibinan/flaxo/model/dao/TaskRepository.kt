package com.tcibinan.flaxo.model.dao

import com.tcibinan.flaxo.model.entity.CourseEntity
import com.tcibinan.flaxo.model.entity.TaskEntity
import org.springframework.data.repository.CrudRepository

interface TaskRepository : CrudRepository<TaskEntity, Long> {
    fun findAllByCourse(course: CourseEntity): Set<TaskEntity>
}