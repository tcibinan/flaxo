package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.entity.CourseEntity
import com.tcibinan.flaxo.core.entity.StudentEntity
import org.springframework.data.repository.CrudRepository

interface StudentRepository : CrudRepository<StudentEntity, Long> {
    fun findByCourse(course: CourseEntity): Set<StudentEntity>
}