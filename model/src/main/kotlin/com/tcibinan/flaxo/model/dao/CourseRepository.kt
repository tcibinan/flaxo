package com.tcibinan.flaxo.model.dao

import com.tcibinan.flaxo.model.data.Course
import com.tcibinan.flaxo.model.data.User
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for course entity.
 */
interface CourseRepository : CrudRepository<Course, Long> {
    fun findByNameAndUser(name: String, user: User): Course?
    fun findByUser(user: User): Set<Course>
}