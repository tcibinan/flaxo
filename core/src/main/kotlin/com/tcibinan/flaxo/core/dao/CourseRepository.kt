package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.model.Course
import org.springframework.data.repository.CrudRepository

interface CourseRepository : CrudRepository<Course, Long> {
}