package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.model.CourseEntity
import org.springframework.data.repository.CrudRepository

interface CourseRepository : CrudRepository<CourseEntity, Long> {
}