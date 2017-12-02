package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.model.Course
import com.tcibinan.flaxo.core.model.CourseEntity
import com.tcibinan.flaxo.core.model.User
import org.springframework.data.repository.CrudRepository

interface CourseRepository : CrudRepository<CourseEntity, Long> {
    fun findByNameAndUser(name: String, user: User): Course?
}