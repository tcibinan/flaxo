package com.tcibinan.flaxo.model.dao

import com.tcibinan.flaxo.model.entity.CourseEntity
import com.tcibinan.flaxo.model.entity.UserEntity
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for course entity.
 */
interface CourseRepository : CrudRepository<CourseEntity, Long> {
    fun findByNameAndUser(name: String, user: UserEntity): CourseEntity?
    fun findByUser(user: UserEntity): Set<CourseEntity>
}