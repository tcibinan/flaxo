package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.model.CourseEntity
import com.tcibinan.flaxo.core.model.UserEntity
import org.springframework.data.repository.CrudRepository

interface CourseRepository : CrudRepository<CourseEntity, Long> {
    fun findByNameAndUser(name: String, user: UserEntity): CourseEntity?
    fun findByUser(user: UserEntity): Set<CourseEntity>
}