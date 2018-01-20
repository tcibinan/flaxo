package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.entity.CourseEntity
import com.tcibinan.flaxo.core.entity.UserEntity
import org.springframework.data.repository.CrudRepository

interface CourseRepository : CrudRepository<CourseEntity, Long> {
    fun findByNameAndUser(name: String, user: UserEntity): CourseEntity?
    fun findByUser(user: UserEntity): Set<CourseEntity>
}